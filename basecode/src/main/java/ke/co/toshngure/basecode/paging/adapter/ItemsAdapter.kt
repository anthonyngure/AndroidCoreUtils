package ke.co.toshngure.basecode.paging.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ke.co.toshngure.basecode.paging.data.ItemRepository
import ke.co.toshngure.basecode.paging.sync.SyncState
import ke.co.toshngure.basecode.paging.sync.SyncStatus

class ItemsAdapter<LoadedModel>(
        diffUtil: DiffUtil.ItemCallback<LoadedModel>,
        @LayoutRes private val layoutRes: Int,
        private val getItemViewHolder: (View) -> BaseItemViewHolder<LoadedModel>,
        private val getItemOnClickListener: OnItemClickListener<LoadedModel>?,
        private val itemRepository: ItemRepository<*, LoadedModel>
) : PagedListAdapter<LoadedModel, RecyclerView.ViewHolder>(diffUtil) {


    interface OnItemClickListener<T> {
        fun onClick(item: T)


    }

    private var syncState: SyncState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_ITEM -> getItemViewHolder(inflate(parent, layoutRes))
            ITEM_TYPE_NETWORK_STATE -> NetworkStateViewHolder.create(parent, itemRepository)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_ITEM -> {
                val item = getItem(position)
                @Suppress("UNCHECKED_CAST")
                val itemHolder = holder as BaseItemViewHolder<LoadedModel>
                item?.let {
                    itemHolder.bindTo(item)
                    if (getItemOnClickListener != null) {
                        holder.itemView.setOnClickListener { getItemOnClickListener.onClick(item) }
                    } else {
                        holder.itemView.setOnClickListener(null)
                    }
                }
            }
            ITEM_TYPE_NETWORK_STATE -> {
                syncState?.let {
                    (holder as NetworkStateViewHolder).bindTo(it)
                }
            }
        }
    }

    /*override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        BeeLog.i(TAG, "onBindViewHolder, position -> $position, payloads -> $payloads")
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            item?.let {
                @Suppress("UNCHECKED_CAST")
                (holder as BaseItemViewHolder<LoadedModel>).update(it)
            }
        } else {
            onBindViewHolder(holder, position)
        }
        //onBindViewHolder(holder, position)
    }*/

    private fun shouldShowBottom(): Boolean {
        return syncState?.let {
            val syncStatus = SyncStatus.valueOf(it.status)
            syncStatus == SyncStatus.LOADING_BEFORE ||
                    syncStatus == SyncStatus.LOADING_BEFORE_EXHAUSTED ||
                    syncStatus == SyncStatus.LOADING_BEFORE_FAILED
        } ?: false
    }

    override fun getItemViewType(position: Int): Int {
        return if (shouldShowBottom() && position == (itemCount - 1)) {
            ITEM_TYPE_NETWORK_STATE
        } else ITEM_TYPE_ITEM

    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (shouldShowBottom()) 1 else 0
    }

    fun setSyncState(newSyncState: SyncState?) {
        val previousState = this.syncState
        val hadExtraRow = shouldShowBottom()
        this.syncState = newSyncState
        val hasExtraRow = shouldShowBottom()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newSyncState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun getItemId(position: Int): Long {
        return when (getItemViewType(position)) {
            ITEM_TYPE_ITEM -> {
                val item = getItem(position)
                item?.let {
                    itemRepository.getItemId(it)
                } ?: super.getItemId(position)
            }
            else -> super.getItemId(position)
        }
    }

    companion object {
        private const val TAG = "ItemsAdapter"
        private const val ITEM_TYPE_ITEM = 0
        private const val ITEM_TYPE_NETWORK_STATE = 1
        fun inflate(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
            return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        }
    }

}
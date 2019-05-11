package ke.co.toshngure.basecode.dataloading.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.sync.SyncState
import ke.co.toshngure.basecode.dataloading.sync.SyncStatus
import ke.co.toshngure.basecode.extensions.hide
import ke.co.toshngure.basecode.extensions.show
import ke.co.toshngure.basecode.logging.BeeLog
import kotlinx.android.synthetic.main.basecode_item_network_state.view.*

/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateViewHolder(view: View, private val itemRepository: ItemRepository<*, *>) :
    RecyclerView.ViewHolder(view) {


    init {
        itemView.errorLayout.setOnClickListener { itemRepository.retry() }
        itemView.noDataLayout.setOnClickListener { itemRepository.retry() }
    }

    fun bindTo(item: SyncState) {
        BeeLog.i(TAG, item)
        itemView.loadingLayout.hide()
        itemView.errorLayout.hide()
        itemView.noDataLayout.hide()
        when (SyncStatus.valueOf(item.status)) {
            SyncStatus.LOADING_BEFORE -> {
                itemView.loadingLayout.show()
            }
            SyncStatus.LOADING_BEFORE_FAILED -> {
                itemView.errorLayout.show()
                itemView.errorMessageTV.text = item.error
            }
            SyncStatus.LOADING_BEFORE_EXHAUSTED -> {
                itemView.noDataLayout.show()
            }
            else -> {
            }
        }
    }

    companion object {
        private const val TAG = "NetworkStateViewHolder"
        fun create(parent: ViewGroup, itemRepository: ItemRepository<*, *>): NetworkStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.basecode_item_network_state, parent, false)
            return NetworkStateViewHolder(view, itemRepository)
        }
    }
}

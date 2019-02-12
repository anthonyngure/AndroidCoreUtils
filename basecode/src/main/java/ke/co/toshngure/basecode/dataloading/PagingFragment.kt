package ke.co.toshngure.basecode.dataloading

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.app.BaseAppFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.sync.SyncStatus
import ke.co.toshngure.basecode.dataloading.viewmodel.ItemListViewModel
import ke.co.toshngure.basecode.extensions.hide
import ke.co.toshngure.basecode.extensions.show
import kotlinx.android.synthetic.main.basecode_fragment_paging.*
import kotlinx.android.synthetic.main.fragment_base.*


abstract class PagingFragment<Model, LoadedModel, D> : BaseAppFragment<D>() {

    private lateinit var mItemListViewModel: ItemListViewModel<Model, LoadedModel>
    private lateinit var mConfig: PagingFragmentConfig<Model, LoadedModel>
    private lateinit var mItemRepository: ItemRepository<Model, LoadedModel>
    protected lateinit var mAdapter: ItemsAdapter<LoadedModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mConfig = getConfig()
        mItemRepository = mConfig.repository
    }


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        LayoutInflater.from(container.context).inflate(R.layout.basecode_fragment_paging, container, true)

        listRV.apply {
            layoutManager = LinearLayoutManager(listRV.context)
            // itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
            itemAnimator = SlideInUpAnimator()
        }
        if (mConfig.withDivider) {
            listRV.addItemDecoration(DividerItemDecoration(listRV.context, DividerItemDecoration.VERTICAL))
        }

        onSetUpRecyclerView(listRV)

        noDataLayout.setOnClickListener { mItemRepository.retry() }

        errorLayout.setOnClickListener { mItemRepository.retry() }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        mItemListViewModel = getViewModel() as ItemListViewModel<Model, LoadedModel>

        mAdapter = ItemsAdapter(
            mConfig.diffUtilItemCallback, mConfig.layoutRes, this::retry,
            this::createItemViewHolder, mConfig.itemClickListener
        )

        listRV.adapter = mAdapter

        mItemListViewModel.syncState.observe(this, Observer {

            mAdapter.setSyncState(it)

            syncingProgressBar.hide()

            loadingLayout.hide()
            noDataLayout.hide()
            errorLayout.hide()

            it?.let { syncState ->

                val syncStatus = SyncStatus.valueOf(syncState.status)
                statusTV.text = syncStatus.name

                when (syncStatus) {


                    //region INITIAL DATA
                    SyncStatus.LOADING_INITIAL -> {
                        loadingLayout.show()
                    }
                    SyncStatus.LOADING_INITIAL_FAILED -> {
                        errorLayout.show()
                        errorMessageTV.text = syncState.error
                    }
                    SyncStatus.LOADING_INITIAL_EXHAUSTED -> {
                        noDataLayout.show()
                    }
                    //endregion

                    //region NEWER DATA
                    SyncStatus.LOADING_AFTER -> {

                    }
                    SyncStatus.LOADING_AFTER_FAILED -> {

                    }
                    SyncStatus.LOADING_AFTER_EXHAUSTED -> {
                    }
                    //endregion


                    SyncStatus.SYNCING -> {
                        syncingProgressBar.show()
                    }
                    SyncStatus.SYNCING_FAILED -> {
                    }
                    SyncStatus.LOADED -> {
                    }
                    SyncStatus.REFRESHING -> {
                        swipeRefreshLayout.isRefreshing = true
                    }
                    SyncStatus.REFRESHING_FAILED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    else -> {
                    }
                }
            } ?: loadingLayout.show()
        })

        mItemListViewModel.items.observe(this, Observer {
            mAdapter.submitList(it)
        })

        mItemListViewModel.init(mItemRepository)
    }


    private fun getViewModel(): ItemListViewModel<*, *> {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <M : ViewModel?> create(modelClass: Class<M>): M {
                @Suppress("UNCHECKED_CAST")
                return ItemListViewModel<Model, LoadedModel>() as M
            }
        })[ItemListViewModel::class.java]
    }


    override fun onSetUpSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        super.onSetUpSwipeRefreshLayout(swipeRefreshLayout)
        swipeRefreshLayout.isEnabled = mConfig.refreshEnabled
        swipeRefreshLayout.setOnRefreshListener {
            mItemRepository.refresh()
        }
    }


    private fun retry() {
        mItemRepository.retry()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_paging_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sync -> {
                mItemRepository.sync()
                true
            }
            R.id.action_clear -> {
                mItemRepository.clear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected open fun onSetUpRecyclerView(recyclerView: RecyclerView) {}

    protected abstract fun getConfig(): PagingFragmentConfig<Model, LoadedModel>

    protected abstract fun createItemViewHolder(itemView: View): BaseItemViewHolder<LoadedModel>


}



package ke.co.toshngure.basecode.dataloading

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.app.BaseAppFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.sync.SyncState
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.dataloading.sync.SyncStatus
import ke.co.toshngure.basecode.dataloading.viewmodel.ItemListViewModel
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.extensions.executeAsync
import ke.co.toshngure.extensions.hide
import ke.co.toshngure.extensions.show
import ke.co.toshngure.extensions.showIf
import kotlinx.android.synthetic.main.basecode_fragment_base_app.*
import kotlinx.android.synthetic.main.basecode_fragment_paging.*


abstract class PagingFragment<Model, FetchedDatabaseModel, FetchedNetworkModel> :
    BaseAppFragment<FetchedNetworkModel>() {

    private lateinit var mConfig: PagingConfig<Model, FetchedDatabaseModel>
    private lateinit var mItemListViewModel: ItemListViewModel<Model, FetchedDatabaseModel>
    private lateinit var mItemRepository: ItemRepository<Model, FetchedDatabaseModel>
    private lateinit var mAdapter: ItemsAdapter<FetchedDatabaseModel>


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        LayoutInflater.from(container.context)
            .inflate(R.layout.basecode_fragment_paging, container, true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mConfig = getPagingConfig()

        mItemRepository = mConfig.repository

        mAdapter = ItemsAdapter(
            mConfig.diffUtilItemCallback, mConfig.layoutRes,
            this::createItemViewHolder, mConfig.itemClickListener, mItemRepository
        )

        mAdapter.setHasStableIds(true)

        listRV.apply {
            layoutManager = LinearLayoutManager(listRV.context)
            adapter = mAdapter
        }

        if (mConfig.itemAnimator != null) {
            listRV.itemAnimator = mConfig.itemAnimator
        }

        if (mConfig.withDivider) {
            listRV.addItemDecoration(
                DividerItemDecoration(
                    listRV.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        onSetUpRecyclerView(listRV)

        noDataLayout.setOnClickListener {
            mConfig.noDataLayoutClickLister?.onClick(it) ?: mItemRepository.retry()
        }

        errorLayout.setOnClickListener {
            mConfig.errorLayoutClickLister?.onClick(it) ?: mItemRepository.retry()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        @Suppress("UNCHECKED_CAST")
        mItemListViewModel = getViewModel() as ItemListViewModel<Model, FetchedDatabaseModel>

        mItemListViewModel.items.observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it)
        })

        mItemListViewModel.syncState.observe(viewLifecycleOwner, Observer {
            onSyncStateChanged(it)
        })

    }

    override fun onStart() {
        super.onStart()
        mItemListViewModel.loadWithArgs(mConfig.arguments)
    }

    private fun onSyncStateChanged(it: SyncState?) {
        BeeLog.i(TAG, it)

        swipeRefreshLayout.isRefreshing = false

        mAdapter.setSyncState(it)

        loadingLayout.hide()
        noDataLayout.hide()
        errorLayout.hide()

        // noDataMessageTV.text = Spanny().append(it.toString())

        it?.let { syncState ->

            val syncStatus = SyncStatus.valueOf(syncState.status)
            statusTV.text = syncStatus.name

            when (syncStatus) {
                SyncStatus.REFRESHING -> {
                    swipeRefreshLayout?.isRefreshing = true
                }

                SyncStatus.REFRESHING_FAILED -> {
                    swipeRefreshLayout?.isRefreshing = false
                }

                //region INITIAL DATA
                SyncStatus.LOADING_INITIAL -> {
                    loadingLayout?.show()
                }
                SyncStatus.LOADING_INITIAL_FAILED -> {
                    errorLayout?.show()
                    errorMessageTV?.text = syncState.error
                }
                SyncStatus.LOADING_INITIAL_EXHAUSTED -> {
                    noDataLayout?.showIf(mLoadingConfig.showNoDataLayout)
                }
                //endregion

                //region OLD DATA
                SyncStatus.LOADING_AFTER -> {

                }
                SyncStatus.LOADING_AFTER_FAILED -> {

                }
                SyncStatus.LOADING_AFTER_EXHAUSTED -> {
                }
                //endregion

                SyncStatus.LOADED -> {
                    noDataLayout.showIf(mLoadingConfig.showNoDataLayout && mAdapter.itemCount == 0)
                }
                else -> {

                }
            }
        } ?: run {
            BeeLog.i(TAG, "SyncState is null")
            loadingLayout?.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        executeAsync {
            val model = mItemRepository.getSyncClass().simpleName
            SyncStatesDatabase.getInstance().syncStates().delete(model, mItemRepository.getTab())
        }
    }


    private fun getViewModel(): ItemListViewModel<*, *> {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <M : ViewModel?> create(modelClass: Class<M>): M {
                @Suppress("UNCHECKED_CAST")
                return ItemListViewModel(mItemRepository) as M
            }
        })[ItemListViewModel::class.java]
    }

    protected fun loadWithArgs(args: Bundle?) {
        mItemListViewModel.loadWithArgs(args)
    }

    override fun onRefresh() {
        super.onRefresh()
        refresh()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun refresh() {
        mItemRepository.refresh()
    }

    private fun retry() {
        mItemRepository.retry()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (BeeLog.DEBUG) {
            inflater.inflate(R.menu.menu_paging_fragment, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                mItemRepository.clear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected open fun onSetUpRecyclerView(recyclerView: RecyclerView) {}

    protected abstract fun getPagingConfig(): PagingConfig<Model, FetchedDatabaseModel>

    protected abstract fun createItemViewHolder(itemView: View): BaseItemViewHolder<FetchedDatabaseModel>

    companion object {
        private const val TAG = "PagingFragment"
    }

}



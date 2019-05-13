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
import ke.co.toshngure.basecode.extensions.showIf
import ke.co.toshngure.basecode.logging.BeeLog
import kotlinx.android.synthetic.main.basecode_fragment_paging.*
import kotlinx.android.synthetic.main.basecode_fragment_base_app.*


abstract class PagingFragment<Model, LoadedModel, D> : BaseAppFragment<D>() {

    private lateinit var mConfig: PagingConfig<Model, LoadedModel>
    private lateinit var mItemListViewModel: ItemListViewModel<Model, LoadedModel>
    private lateinit var mItemRepository: ItemRepository<Model, LoadedModel>
    private lateinit var mAdapter: ItemsAdapter<LoadedModel>


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        LayoutInflater.from(container.context).inflate(R.layout.basecode_fragment_paging, container, true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mConfig = getPagingConfig()
        mItemRepository = mConfig.repository

        mAdapter = ItemsAdapter(mConfig.diffUtilItemCallback, mConfig.layoutRes,
                this::createItemViewHolder, mConfig.itemClickListener, mItemRepository)
        mAdapter.setHasStableIds(true)


        listRV.apply {
            layoutManager = LinearLayoutManager(listRV.context)
            // itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
            itemAnimator = SlideInUpAnimator()
            adapter = mAdapter
        }
        if (mConfig.withDivider) {
            listRV.addItemDecoration(DividerItemDecoration(listRV.context, DividerItemDecoration.VERTICAL))
        }
        listRV.isNestedScrollingEnabled = false

        onSetUpRecyclerView(listRV)

        noDataLayout.setOnClickListener { mItemRepository.retry() }

        errorLayout.setOnClickListener { mItemRepository.retry() }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        @Suppress("UNCHECKED_CAST")
        mItemListViewModel = getViewModel() as ItemListViewModel<Model, LoadedModel>

        mItemListViewModel.items.observe(this, Observer {
            mAdapter.submitList(it)
        })

        mItemListViewModel.syncState.observe(this, Observer {

            //BeeLog.i(TAG, it)

            swipeRefreshLayout.isRefreshing = false

            mAdapter.setSyncState(it)

            loadingLayout.hide()
            noDataLayout.hide()
            errorLayout.hide()

            it?.let { syncState ->

                val syncStatus = SyncStatus.valueOf(syncState.status)
                statusTV.text = syncStatus.name

                when (syncStatus) {


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
                    }
                    else -> {

                    }
                }
            } ?: run {
                BeeLog.i(TAG, "SyncState is null")
                loadingLayout?.show()
            }
        })

        mItemListViewModel.loadWithArgs(mConfig.arguments)

    }


    private fun getViewModel(): ItemListViewModel<*, *> {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <M : ViewModel?> create(modelClass: Class<M>): M {
                @Suppress("UNCHECKED_CAST")
                return ItemListViewModel(mItemRepository) as M
            }
        })[ItemListViewModel::class.java]
    }


    override fun onSetUpSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        super.onSetUpSwipeRefreshLayout(swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener { onRefresh(swipeRefreshLayout) }
    }

    protected fun loadWithArgs(args: Bundle?) {
        mItemListViewModel.loadWithArgs(args)
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

    protected open fun onRefresh(swipeRefreshLayout: SwipeRefreshLayout?) {
    }

    protected open fun onSetUpRecyclerView(recyclerView: RecyclerView) {}

    protected abstract fun getPagingConfig(): PagingConfig<Model, LoadedModel>

    protected abstract fun createItemViewHolder(itemView: View): BaseItemViewHolder<LoadedModel>

    companion object {
        private const val TAG = "PagingFragment"
    }

}



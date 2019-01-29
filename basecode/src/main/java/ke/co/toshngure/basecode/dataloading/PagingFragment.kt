package ke.co.toshngure.basecode.dataloading

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
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
import ke.co.toshngure.basecode.dataloading.viewmodel.ItemListViewModel
import kotlinx.android.synthetic.main.basecode_fragment_paging.*
import kotlinx.android.synthetic.main.fragment_base.*


abstract class PagingFragment<Model, ModelListing> : BaseAppFragment<Any>() {

    private lateinit var modelList: ItemListViewModel<Model, ModelListing>
    private lateinit var config: Config

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DataManager.sync(Model::class.java)
        setHasOptionsMenu(true)
        config = getConfig()
    }


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        LayoutInflater.from(container.context).inflate(R.layout.basecode_fragment_paging, container, true)


        listRV.apply {
            layoutManager = LinearLayoutManager(listRV.context)
            // itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
            itemAnimator = SlideInUpAnimator()
        }

        if (config.withDivider) listRV.addItemDecoration(
            DividerItemDecoration(
                listRV.context,
                DividerItemDecoration.VERTICAL
            )
        )

        onSetUpRecyclerView(listRV)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        modelList = getViewModel() as ItemListViewModel<Model, ModelListing>

        val adapter = ItemsAdapter(
            createDiffUtilItemCallback(),
            config.layoutRes, this::retry, this::createItemViewHolder, createOnItemClickListener()
        )

        listRV.adapter = adapter

        modelList.items.observe(this, Observer<PagedList<ModelListing>> {
            adapter.submitList(it)
        })
        modelList.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })

        modelList.refreshState.observe(this, Observer {
            swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING
        })
        val repo = createRepository()
        modelList.load(repo)
    }


    private fun getViewModel(): ItemListViewModel<*, *> {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <M : ViewModel?> create(modelClass: Class<M>): M {
                @Suppress("UNCHECKED_CAST")
                return ItemListViewModel<Model, ModelListing>() as M
            }
        })[ItemListViewModel::class.java]
    }


    override fun onSetUpSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        super.onSetUpSwipeRefreshLayout(swipeRefreshLayout)
        swipeRefreshLayout.isEnabled = config.refreshEnabled
        swipeRefreshLayout.setOnRefreshListener {
            modelList.refresh()
        }
    }


    private fun retry() {
        modelList.retry()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_paging_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sync -> {
                toast("Sync")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected open fun onSetUpRecyclerView(recyclerView: RecyclerView) {}

    protected abstract fun createRepository(): ItemRepository<Model, ModelListing>

    protected abstract fun createDiffUtilItemCallback(): DiffUtil.ItemCallback<ModelListing>

    protected abstract fun getConfig(): Config

    protected abstract fun createItemViewHolder(itemView: View): BaseItemViewHolder<ModelListing>

    protected open fun createOnItemClickListener(): ItemsAdapter.OnItemClickListener<ModelListing>? {
        return null
    }

}



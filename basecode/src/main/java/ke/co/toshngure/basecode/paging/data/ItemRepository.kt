package ke.co.toshngure.basecode.paging.data

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ke.co.toshngure.extensions.executeAsync
import retrofit2.Call


abstract class ItemRepository<Model, DatabaseFetchedModel> {


    private var mBoundaryCallback: ItemBoundaryCallback<Model, DatabaseFetchedModel>? = null

    private var mItemRepositoryConfig: ItemRepositoryConfig<Model>

    init {
        mItemRepositoryConfig = this.getItemRepositoryConfig()
    }


    /**
     * Inserts the response into the database.
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    internal fun insertItemsIntoDb(items: List<Model>,args: Bundle?) {
        executeAsync { save(items, args) }
    }


    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, deleteAll
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    internal fun refresh() {
        mBoundaryCallback?.refresh()
    }

    @MainThread
    internal fun retry() {
        mBoundaryCallback?.retry()
    }

    /**
     * Returns a Listing for posts.
     */
    @MainThread
    fun list(args: Bundle?): LiveData<PagedList<DatabaseFetchedModel>> {

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        mBoundaryCallback = ItemBoundaryCallback(this, args)

        //Get config
        val dataLoadingConfig = getItemRepositoryConfig()

        // create a data source factory from Room
        val dataSourceFactory = getItemDataSource(args)

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(dataLoadingConfig.dbPerPage)
            .build()


        val builder = LivePagedListBuilder(dataSourceFactory, config)
            .setBoundaryCallback(mBoundaryCallback)

        return builder.build()
    }


    open fun getAPICall(itemAtBottomId: Long, itemAtTopId: Long, args: Bundle?): Call<List<Model>>? {
        return null
    }

    open fun getRefreshAPICall(args: Bundle?): Call<List<Model>>? {
        return null
    }

    /**
     * To save items into the db, called inside a transaction in background
     */
    protected open fun save(items: List<Model>, args: Bundle?) {
        getItemDao().insert(items)
    }

    /**
     * To delete all cached items
     */
    open fun deleteAll() {}


    /**
     * To delete all cached items
     */
    internal fun clear() {
        executeAsync {
            deleteAll()
        }
    }

    fun getSyncClass(): Class<Model> {
        return mItemRepositoryConfig.syncClass
    }

    open fun getTab(): String {
        return ""
    }

    open fun getPaginates(): Boolean {
        return true
    }

    /**
     * To get id of an item
     */
    abstract fun getItemId(item: DatabaseFetchedModel): Long


    abstract fun getItemRepositoryConfig(): ItemRepositoryConfig<Model>

    abstract fun getItemDataSource(args: Bundle?): DataSource.Factory<Int, DatabaseFetchedModel>

    abstract fun getItemDao(): ItemDao<Model>

    companion object {
        const val TAG = "ItemRepository"
    }
}
package ke.co.toshngure.basecode.dataloading.data

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call


abstract class ItemRepository<Model, LoadedModel> constructor(val db: RoomDatabase) {

    private lateinit var mBoundaryCallback: ItemBoundaryCallback<Model, LoadedModel>

    /**
     * Inserts the response into the database.
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    internal fun insertItemsIntoDb(items: List<Model>) {
        runBlocking(Dispatchers.IO) {
            db.runInTransaction { save(items) }
        }
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
        mBoundaryCallback.refresh()
    }

    @MainThread
    internal fun retry() {
        mBoundaryCallback.retry()
    }

    /**
     * To do a sync for changes of all cached items
     */
    @MainThread
    internal fun sync() {
        mBoundaryCallback.sync()
    }

    /**
     * Returns a Listing for posts.
     */
    @MainThread
    fun list(): LiveData<PagedList<LoadedModel>> {

        //Get config
        val dataLoadingConfig = getDataLoadingConfig()

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        mBoundaryCallback = ItemBoundaryCallback(this)

        // create a data source factory from Room
        val dataSourceFactory = index()

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(dataLoadingConfig.dbPerPage)
            .build()


        val builder = LivePagedListBuilder(dataSourceFactory, config)
            .setBoundaryCallback(mBoundaryCallback)

        return builder.build()
    }


    abstract fun getAPICall(before: Long, after: Long): Call<List<Model>>

    /**
     * Get a paged data source factory
     */
    protected abstract fun index(): DataSource.Factory<Int, LoadedModel>


    /**
     * To save items into the db, called inside a transaction in background
     */
    protected open fun save(items: List<Model>) {
        getDao().insert(items)
    }

    /**
     * To delete all cached items
     */
    protected open fun deleteAll() {}


    /**
     * To delete all cached items
     */
    open fun clear() {
        runBlocking(Dispatchers.IO) {
            db.runInTransaction { deleteAll() }
        }
    }

    /**
     * To get id of an item
     */
    protected abstract fun getDao(): ItemDao<Model>

    /**
     * To get id of an item
     */
    abstract fun getItemId(item: LoadedModel): Long

    abstract fun getSyncClass(): Class<Model>

    /**
     * To delete all cached items, called inside a transaction in background
     */
    internal fun getSyncId(): String {
        return getSyncClass().simpleName
    }


    internal open fun getDataLoadingConfig(): DataLoadingConfig {
        return DataLoadingConfig()
    }
}
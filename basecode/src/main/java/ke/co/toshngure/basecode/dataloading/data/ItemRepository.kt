package ke.co.toshngure.basecode.dataloading.data

import android.os.AsyncTask
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.RoomDatabase
import ke.co.toshngure.basecode.dataloading.NetworkState
import ke.co.toshngure.basecode.dataloading.util.Listing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors


abstract class ItemRepository<Model, DaoModel> constructor(private val db: RoomDatabase) {


    private val ioExecutor = Executors.newFixedThreadPool(5)


    /**
     * Inserts the response into the database.
     */
    private fun insertResultIntoDb(items: List<Model>) {
        db.runInTransaction {
            save(items)
        }

    }


    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        getCall(0, 0)?.enqueue(
                object : Callback<List<Model>> {
                    override fun onFailure(call: Call<List<Model>>, t: Throwable) {
                        // retrofit calls this on main thread so safe to call set value
                        networkState.value = NetworkState.error(t.message)
                    }

                    override fun onResponse(call: Call<List<Model>>, response: Response<List<Model>>) {
                        ioExecutor.execute {

                            db.runInTransaction {
                                clear()
                                response.body()?.let { insertResultIntoDb(it) }
                            }
                            // since we are in bg thread now, post the result.
                            networkState.postValue(NetworkState.LOADED)
                        }
                    }
                }
        )
        return networkState
    }

    /**
     * Returns a Listing for posts.
     */
    @MainThread
    fun list(): Listing<DaoModel> {

        //Get config
        val dataLoadingConfig = getDataLoadingConfig()

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = ItemBoundaryCallback(
                handleResponse = this::insertResultIntoDb,
                ioExecutor = ioExecutor,
                getItemId = this::getItemId,
                getResponseCall = this::getCall,
                dataLoadingConfig = dataLoadingConfig)

        // create a data source factory from Room
        val dataSourceFactory = index()


        val config: PagedList.Config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(dataLoadingConfig.dbPerPage)
                .build()


        val builder = LivePagedListBuilder(dataSourceFactory, config).setBoundaryCallback(boundaryCallback)

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        return Listing<DaoModel>(
                pagedList = builder.build(),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.helper.retryAllFailed()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState,
                clear = {
                    ClearDBAsyncTask<DaoModel>(db, this::clear).execute()
                }
        )
    }

    private class ClearDBAsyncTask<L>(private val db: RoomDatabase, val clearCallback: () -> Unit)
        : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            db.runInTransaction {
                clearCallback()
            }
            return null
        }
    }


    protected open fun getCall(before: Long, after: Long): Call<List<Model>>? = null

    /**
     * Get a paged data source factory
     */
    protected abstract fun index(): DataSource.Factory<Int, DaoModel>


    /**
     * To save items into the db, called inside a transaction in background
     */
    protected open fun save(items: List<Model>) {
        getDao().insert(items)
    }

    /**
     * To delete all cached items, called inside a transaction in background
     */
    protected open fun clear() {
        // getDao().deleteAll()
    }

    /**
     * To get id of an item
     */
    protected abstract fun getDao(): ItemDao<Model>

    /**
     * To get id of an item
     */
    protected abstract fun getItemId(item: DaoModel): Long


    protected open fun getDataLoadingConfig(): DataLoadingConfig {
        return DataLoadingConfig()
    }
}
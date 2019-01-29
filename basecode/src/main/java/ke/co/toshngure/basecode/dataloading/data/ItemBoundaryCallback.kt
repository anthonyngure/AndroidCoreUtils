package ke.co.toshngure.basecode.dataloading.data

import androidx.annotation.MainThread
import androidx.paging.PagedList
import ke.co.toshngure.basecode.dataloading.util.PagingRequestHelper
import ke.co.toshngure.basecode.extensions.createStatusLiveData
import ke.co.toshngure.basecode.logging.BeeLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class ItemBoundaryCallback<Model, ModelListing>(
        private val ioExecutor: Executor,
        private val getItemId: (item: ModelListing) -> Long,
        private val getResponseCall: (before: Long, after: Long) -> Call<List<Model>>?,
        private val handleResponse: (List<Model>) -> Unit,
        private val dataLoadingConfig: DataLoadingConfig)
    : PagedList.BoundaryCallback<ModelListing>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    // Requests initial data from the network, replacing all content currently
    // in the database.
    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        BeeLog.i(TAG, "onZeroItemsLoaded")
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            getResponseCall(0, 0)?.enqueue(createCallback(it))
        }
    }

    // Requests additional data from the network, appending the results to the
    // end of the database's existing data.
    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: ModelListing) {
        super.onItemAtEndLoaded(itemAtEnd)
        BeeLog.i(TAG, "onItemAtEndLoaded, id = " + getItemId(itemAtEnd))
        BeeLog.i(TAG, "onItemAtEndLoaded, isRunning = " + helper.isRunning(PagingRequestHelper.RequestType.AFTER))
        if (dataLoadingConfig.paginates) {
            //When the last item is loaded we will request more data from network if the repo paginates
            helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { it ->
                getResponseCall(getItemId(itemAtEnd), 0)?.enqueue(createCallback(it))
            }
        } else {
            BeeLog.i(TAG, "onItemAtEndLoaded, pagination is disabled!")
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: ModelListing) {
        super.onItemAtFrontLoaded(itemAtFront)
        BeeLog.i(TAG, "onItemAtFrontLoaded, id = " + getItemId(itemAtFront))
        // ignored, since we only ever append to what's in the DB
    }



    private fun createCallback(requestHelperCallback: PagingRequestHelper.Request.Callback): Callback<List<Model>> {

        return object : Callback<List<Model>> {

            override fun onFailure(call: Call<List<Model>>, t: Throwable) {
                requestHelperCallback.recordFailure(t)
            }

            override fun onResponse(call: Call<List<Model>>, response: Response<List<Model>>) {
                if (response.isSuccessful) {
                    response.body()?.let { items -> insertItemsIntoDb(items, requestHelperCallback) }
                            ?: requestHelperCallback.recordFailure(Throwable("Empty response body!"))
                } else {
                    requestHelperCallback.recordFailure(Throwable(response.message()))
                }
            }
        }
    }


    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(items: List<Model>, it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(items)
            it.recordSuccess()
        }
    }

    companion object {
        private const val TAG = "ItemBoundaryCallback"
    }

}
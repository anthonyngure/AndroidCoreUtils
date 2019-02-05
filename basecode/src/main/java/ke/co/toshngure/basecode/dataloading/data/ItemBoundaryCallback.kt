package ke.co.toshngure.basecode.dataloading.data

import android.text.TextUtils
import androidx.annotation.MainThread
import androidx.paging.PagedList
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.dataloading.sync.SyncStatus
import ke.co.toshngure.basecode.logging.BeeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemBoundaryCallback<Model, LoadedModel>(private val repository: ItemRepository<Model, LoadedModel>) :
    PagedList.BoundaryCallback<LoadedModel>() {

    private var mItemAtEndId = 0L
    internal val syncStateHelper = SyncStateHelper(repository)


    /**
     * Database returned 0 items. We should query the backend for initial items.
     * Requests initial data from the network
     */
    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        BeeLog.i(TAG, "onZeroItemsLoaded")
        syncStateHelper.recordStatus(SyncStatus.LOADED)
        syncStateHelper.runIfPossible(SyncStatus.LOADING_INITIAL) {
            repository.getAPICall(0, 0).enqueue(createCallback())
        }
    }

    /**
     * User reached to the end of the list.
     * Requests additional data from the network, appending the results to the end of the database's existing data.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: LoadedModel) {
        super.onItemAtEndLoaded(itemAtEnd)
        mItemAtEndId = repository.getItemId(itemAtEnd)
        BeeLog.i(TAG, "onItemAtEndLoaded, id = $mItemAtEndId")
        // BeeLog.i(TAG, "onItemAtEndLoaded, isRunning = " + helper.isRunning(PagingRequestHelper.RequestType.AFTER))
        if (repository.getItemRepositoryConfig().paginates) {
            //When the last item is loaded we will request more data from network if the repo paginates
            syncStateHelper.runIfPossible(SyncStatus.LOADING_BEFORE) {
                repository.getAPICall(mItemAtEndId, 0).enqueue(createCallback())
            }
        } else {
            BeeLog.i(TAG, "onItemAtEndLoaded, pagination is disabled!")
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: LoadedModel) {
        super.onItemAtFrontLoaded(itemAtFront)
        BeeLog.i(TAG, "onItemAtFrontLoaded, id = " + repository.getItemId(itemAtFront))
        syncStateHelper.recordStatus(SyncStatus.LOADED)
        // ignored, since we only ever append to what's in the DB
    }

    private fun createCallback(): Callback<List<Model>> {

        return object : Callback<List<Model>> {

            override fun onFailure(call: Call<List<Model>>, t: Throwable) {
                syncStateHelper.recordFailure(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Model>>, response: Response<List<Model>>) {
                val data = response.body()
                if (response.isSuccessful && data != null) {
                    if (data.isEmpty()) {
                        syncStateHelper.recordExhausted()
                    } else {
                        repository.insertItemsIntoDb(data)
                        syncStateHelper.recordStatus(SyncStatus.LOADED)
                    }
                } else {
                    syncStateHelper.recordFailure(response.message())
                }
            }
        }
    }

    internal fun sync() {
        //recordStatus(SyncStatus.SYNCING)

        val ids = arrayListOf<Long>()
        for (i in 1..500) {
            ids.add((i + 10000000000000000))
        }
        val data = TextUtils.join(",", ids)
        //BeeLog.i(model, data)
        //BeeLog.i(model, "Data Length = ${data.toByteArray().size}")

        //val state = SyncStatesDatabase.getInstance(context).syncStates().findByModel(model)

        //BeeLog.i(model, state?.toString())
    }

    internal fun refresh() {
        //recordStatus(SyncStatus.REFRESHING)
    }

    internal fun retry() {
        runBlocking(Dispatchers.IO) {
            val syncState = syncStateHelper.loadSyncState()
            val syncStatus = SyncStatus.valueOf(syncState.status)
            syncState.status = SyncStatus.LOADED.value
            SyncStatesDatabase.getInstance().syncStates().update(syncState)
            when (syncStatus) {
                SyncStatus.LOADING_INITIAL_EXHAUSTED,
                SyncStatus.LOADING_INITIAL_FAILED -> {
                    syncStateHelper.runIfPossible(SyncStatus.LOADING_INITIAL) {
                        repository.getAPICall(0, 0).enqueue(createCallback())
                    }
                }
                SyncStatus.LOADING_BEFORE_FAILED,
                SyncStatus.LOADING_BEFORE_EXHAUSTED -> {
                    syncStateHelper.runIfPossible(SyncStatus.LOADING_BEFORE) {
                        repository.getAPICall(mItemAtEndId, 0).enqueue(createCallback())
                    }
                }
                SyncStatus.LOADING_AFTER_FAILED,
                SyncStatus.LOADING_AFTER_EXHAUSTED -> {

                }
                else -> {
                }
            }

        }
    }

    companion object {
        private const val TAG = "ItemBoundaryCallback"
    }

}
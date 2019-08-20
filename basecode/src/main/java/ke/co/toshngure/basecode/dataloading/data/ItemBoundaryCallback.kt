package ke.co.toshngure.basecode.dataloading.data

import androidx.annotation.MainThread
import androidx.paging.PagedList
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.dataloading.sync.SyncStatus
import ke.co.toshngure.basecode.extensions.executeAsync
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemBoundaryCallback<Model, LoadedModel>(private val repository: ItemRepository<Model, LoadedModel>) :
    PagedList.BoundaryCallback<LoadedModel>() {

    private var mItemAtEndId = 0L
    internal val syncStateHelper = SyncStateHelper(repository.getSyncClass(), repository.getTab())


    /**
     * Database returned 0 items. We should query the backend for initial items.
     * Requests initial data from the network
     */
    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        BeeLog.i(getTag(), "onZeroItemsLoaded")
        repository.getAPICall(0, 0)?.let {
            syncStateHelper.recordStatus(SyncStatus.LOADED)
            syncStateHelper.runIfPossible(SyncStatus.LOADING_INITIAL) {
                it.enqueue(createCallback())
            }
        } ?: run {
            BeeLog.i(getTag(), "onZeroItemsLoaded, connection is disabled!")
            syncStateHelper.recordStatus(SyncStatus.LOADING_INITIAL_EXHAUSTED)
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

        BeeLog.i(getTag(), "onItemAtEndLoaded, id = $mItemAtEndId")

        repository.getAPICall(mItemAtEndId, 0)?.let {
            if (repository.getPaginates()) {
                //When the last item is loaded we will request more data from network if the repo paginates
                syncStateHelper.runIfPossible(SyncStatus.LOADING_BEFORE) {
                    it.enqueue(createCallback())
                }
            } else {
                BeeLog.i(getTag(), "onItemAtEndLoaded, pagination is disabled!")
            }
        } ?: run {
            BeeLog.i(getTag(), "onZeroItemsLoaded, connection is disabled!")
        }


    }

    override fun onItemAtFrontLoaded(itemAtFront: LoadedModel) {
        super.onItemAtFrontLoaded(itemAtFront)
        BeeLog.i(getTag(), "onItemAtFrontLoaded, id = " + repository.getItemId(itemAtFront))
        syncStateHelper.recordStatus(SyncStatus.LOADED)
        // ignored, since we only ever append to what's in the DB
    }

    private fun createCallback(): Callback<List<Model>> {

        return object : Callback<List<Model>> {

            override fun onFailure(call: Call<List<Model>>, t: Throwable) {
                if (BeeLog.DEBUG) {
                    syncStateHelper.recordFailure(t.localizedMessage)
                } else {
                    val error = NetworkUtils.getCallback().getErrorMessageFromResponseBody(0, null)
                    syncStateHelper.recordFailure(error)
                }
            }

            override fun onResponse(call: Call<List<Model>>, response: Response<List<Model>>) {
                val data = response.body()
                if (response.isSuccessful && data != null) {
                    if (data.isEmpty()) {
                        BeeLog.i(getTag(), "Data empty, recordExhausted()")
                        syncStateHelper.recordExhausted()
                    } else {
                        executeAsync {
                            val syncState = syncStateHelper.loadSyncState()
                            // When refreshing we have to delete
                            if (SyncStatus.valueOf(syncState.status) == SyncStatus.REFRESHING) {
                                repository.deleteAll()
                            }
                            repository.insertItemsIntoDb(data)
                            syncStateHelper.recordStatus(SyncStatus.LOADED)
                        }

                    }

                } else {
                    val errorBody = response.errorBody()
                    errorBody?.let {
                        val errorMessage = NetworkUtils.getCallback()
                            .getErrorMessageFromResponseBody(response.code(), it)
                        syncStateHelper.recordFailure(errorMessage)
                    } ?: syncStateHelper.recordFailure(response.message())
                }
            }
        }
    }


    internal fun refresh() {
        repository.getRefreshAPICall()?.let {
            // Refresh data
            syncStateHelper.runIfPossible(SyncStatus.REFRESHING) {
                it.enqueue(createCallback())
            }
        } ?: run {
            syncStateHelper.recordStatus(SyncStatus.LOADED)
            BeeLog.w(getTag(), "refresh, refresh call is null!")
        }
    }

    internal fun retry() {
        executeAsync {
            val syncState = syncStateHelper.loadSyncState()
            val syncStatus = SyncStatus.valueOf(syncState.status)
            syncState.status = SyncStatus.LOADED.value
            SyncStatesDatabase.getInstance().syncStates().update(syncState)
            when (syncStatus) {
                SyncStatus.LOADING_INITIAL_EXHAUSTED,
                SyncStatus.LOADING_INITIAL_FAILED -> {
                    // If it does not connect, just ignore the retry
                    repository.getAPICall(0, 0)?.let {
                        syncStateHelper.runIfPossible(SyncStatus.LOADING_INITIAL) {
                            it.enqueue(createCallback())
                        }
                    } ?: run {
                        BeeLog.i(getTag(), "retry, connection is disabled!")
                        // Try refresh if a refresh call is provided
                        repository.getRefreshAPICall()?.let {
                            syncStateHelper.runIfPossible(SyncStatus.REFRESHING) {
                                it.enqueue(createCallback())
                            }
                        } ?: run {
                            syncStateHelper.recordStatus(SyncStatus.LOADING_INITIAL_EXHAUSTED)
                        }

                    }
                }
                SyncStatus.LOADING_BEFORE_FAILED,
                SyncStatus.LOADING_BEFORE_EXHAUSTED -> {
                    syncStateHelper.runIfPossible(SyncStatus.LOADING_BEFORE) {
                        repository.getAPICall(mItemAtEndId, 0)?.enqueue(createCallback())
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

    fun getTag(): String {
        return "ItemBoundaryCallback-${repository.getSyncClass().simpleName}-${repository.getTab()}"
    }

}
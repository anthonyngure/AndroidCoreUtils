package ke.co.toshngure.basecode.dataloading.data

import androidx.annotation.WorkerThread
import ke.co.toshngure.basecode.dataloading.sync.SyncState
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.dataloading.sync.SyncStatus
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.extensions.executeAsync

class SyncStateHelper<Model>(private val syncClass: Class<Model>, private val syncTab: String = "") {


    fun runIfPossible(syncStatus: SyncStatus, request: () -> Unit) {
        BeeLog.i(TAG, "runIfPossible -> $syncStatus")
        executeAsync {
            val syncState = loadSyncState()
            BeeLog.i(TAG, "runIfPossible, current state -> $syncState")
            if (syncState.status != syncStatus.value && !stateIsExhaustedOrFailed(syncStatus)) {
                recordStatus(syncStatus)
                request.invoke()
            }
        }
    }


    private fun stateIsExhaustedOrFailed(syncStatus: SyncStatus): Boolean {
        val syncState = loadSyncState()
        return when (syncStatus) {
            SyncStatus.LOADING_INITIAL -> {
                syncState.status == SyncStatus.LOADING_INITIAL_EXHAUSTED.value ||
                        syncState.status == SyncStatus.LOADING_INITIAL_FAILED.value
            }
            SyncStatus.LOADING_BEFORE -> {
                syncState.status == SyncStatus.LOADING_BEFORE_EXHAUSTED.value ||
                        syncState.status == SyncStatus.LOADING_BEFORE_FAILED.value
            }
            SyncStatus.LOADING_AFTER -> {
                syncState.status == SyncStatus.LOADING_AFTER_EXHAUSTED.value ||
                        syncState.status == SyncStatus.LOADING_AFTER_FAILED.value
            }
            else -> false
        }
    }

    fun recordStatus(syncStatus: SyncStatus) {
        BeeLog.i(TAG, "recordStatus -> $syncStatus")
        executeAsync {
            val syncState = loadSyncState()
            syncState.status = syncStatus.value
            SyncStatesDatabase.getInstance().syncStates().update(syncState)
        }
    }

    internal fun recordFailure(error: String?) {
        executeAsync {
            val syncState = loadSyncState()
            when (SyncStatus.valueOf(syncState.status)) {
                SyncStatus.LOADING_INITIAL -> syncState.status = SyncStatus.LOADING_INITIAL_FAILED.value
                SyncStatus.LOADING_BEFORE -> syncState.status = SyncStatus.LOADING_BEFORE_FAILED.value
                SyncStatus.LOADING_AFTER -> syncState.status = SyncStatus.LOADING_AFTER_FAILED.value
                SyncStatus.REFRESHING -> syncState.status = SyncStatus.REFRESHING_FAILED.value
                else -> {
                }
            }
            syncState.error = error
            SyncStatesDatabase.getInstance().syncStates().update(syncState)
        }
    }

    internal fun recordExhausted() {
        executeAsync {
            val syncState = loadSyncState()
            val syncStatus = SyncStatus.valueOf(syncState.status)
            when (syncStatus) {
                SyncStatus.LOADING_INITIAL -> syncState.status = SyncStatus.LOADING_INITIAL_EXHAUSTED.value
                SyncStatus.LOADING_BEFORE -> syncState.status = SyncStatus.LOADING_BEFORE_EXHAUSTED.value
                SyncStatus.LOADING_AFTER -> syncState.status = SyncStatus.LOADING_AFTER_EXHAUSTED.value
                else -> {
                }
            }

            SyncStatesDatabase.getInstance().syncStates().update(syncState)
        }
    }

    @WorkerThread
    fun loadSyncState(): SyncState {
        val model = syncClass.simpleName
        val tab = syncTab
        var syncState = SyncStatesDatabase.getInstance().syncStates().find(model, tab)
        if (syncState == null) {
            syncState = SyncState(model, tab, status = SyncStatus.LOADED.value)
            SyncStatesDatabase.getInstance().syncStates().insert(syncState)
        }
        return syncState
    }

    companion object {
        private const val TAG = "SyncStateHelper"
    }
}
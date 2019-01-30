package ke.co.toshngure.basecode.dataloading.data

import android.text.TextUtils
import androidx.annotation.WorkerThread
import ke.co.toshngure.basecode.dataloading.sync.SyncState
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.dataloading.sync.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal class SyncStateHelper<Model, LoadedModel>(private val repository: ItemRepository<Model, LoadedModel>) {



    fun runIfPossible(syncStatus: SyncStatus, request: () -> Unit) {
        runBlocking(Dispatchers.IO) {
            val syncState = loadSyncState()
            if (syncState.status != syncStatus.value && !stateIsExhaustedOrFailed(syncStatus)){
                recordStatus(syncStatus)
                request.invoke()
            }
        }
    }

    private fun stateIsExhaustedOrFailed(syncStatus: SyncStatus) : Boolean{
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

    internal fun recordStatus(syncStatus: SyncStatus) {
        runBlocking(Dispatchers.IO) {
            val syncState = loadSyncState()
            syncState.status = syncStatus.value
            SyncStatesDatabase.getInstance().syncStates().update(syncState)
        }
    }

    internal fun recordFailure(error: String) {
        runBlocking(Dispatchers.IO) {
            val syncState = loadSyncState()
            val syncStatus = SyncStatus.valueOf(syncState.status)
            when (syncStatus) {
                SyncStatus.LOADING_INITIAL -> syncState.status = SyncStatus.LOADING_INITIAL_FAILED.value
                SyncStatus.LOADING_BEFORE -> syncState.status = SyncStatus.LOADING_BEFORE_FAILED.value
                SyncStatus.LOADING_AFTER -> syncState.status = SyncStatus.LOADING_AFTER_FAILED.value
                SyncStatus.SYNCING -> syncState.status = SyncStatus.SYNCING_FAILED.value
                SyncStatus.REFRESHING -> syncState.status = SyncStatus.REFRESHING_FAILED.value
                else -> {
                }
            }
            syncState.error = error
            SyncStatesDatabase.getInstance().syncStates().update(syncState)
        }
    }

    internal fun recordExhausted() {
        runBlocking(Dispatchers.IO) {
            val syncState = loadSyncState()
            val syncStatus = SyncStatus.valueOf(syncState.status)
            when (syncStatus) {
                SyncStatus.LOADING_INITIAL -> syncState.status = SyncStatus.LOADING_INITIAL_EXHAUSTED.value
                SyncStatus.LOADING_BEFORE -> syncState.status = SyncStatus.LOADING_BEFORE_EXHAUSTED.value
                SyncStatus.LOADING_AFTER -> syncState.status = SyncStatus.LOADING_AFTER_EXHAUSTED.value
                else -> { }
            }

            SyncStatesDatabase.getInstance().syncStates().update(syncState)
        }
    }

    @WorkerThread
    internal fun loadSyncState(): SyncState {
        var syncState = SyncStatesDatabase.getInstance().syncStates().findByModel(repository.getSyncId())
        if (syncState == null) {
            syncState = SyncState(repository.getSyncId(), status = SyncStatus.LOADED.value)
            SyncStatesDatabase.getInstance().syncStates().insert(syncState)
        }
        return syncState
    }
}
package ke.co.toshngure.basecode.dataloading.sync

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ke.co.toshngure.basecode.dataloading.data.ItemDao

@Dao
interface SyncStateDao : ItemDao<SyncState> {

    @Query("SELECT * FROM sync_states WHERE model = :model AND tab = :tab")
    fun find(model: String, tab: String = ""): SyncState?

    @Query("SELECT * FROM sync_states WHERE model = :model AND tab = :tab")
    fun findLive(model: String, tab: String = ""): LiveData<SyncState?>

    @Query("DELETE FROM sync_states")
    fun deleteAll()
}
package ke.co.toshngure.basecode.dataloading.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Anthony Ngure on 20/05/2018.
 * Email : anthonyngure25@gmail.com.
 */
@Entity(tableName = "sync_states")
data class SyncState(
    @PrimaryKey var model: String,
    var maxCache: Long = 500,
    var lastSyncTimestamp: Long = 0,
    var status: String,
    var error: String? = null
)

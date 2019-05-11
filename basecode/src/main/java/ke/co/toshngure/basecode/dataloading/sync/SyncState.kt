package ke.co.toshngure.basecode.dataloading.sync

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.co.toshngure.basecode.util.DatesHelper
import java.util.*

/**
 * Created by Anthony Ngure on 20/05/2018.
 * Email : anthonyngure25@gmail.com.
 */
@Entity(tableName = "sync_states", primaryKeys = ["model", "tab"])
data class SyncState(
        var model: String,
        var tab: String = "",
        var maxCache: Long = 500,
        var newCount: Long = 0,
        var status: String,
        var error: String? = null
)

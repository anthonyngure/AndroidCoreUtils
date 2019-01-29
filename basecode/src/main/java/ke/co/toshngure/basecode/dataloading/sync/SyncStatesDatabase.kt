package ke.co.toshngure.basecode.dataloading.sync

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SyncState::class], version = 1, exportSchema = false)
abstract class SyncStatesDatabase : RoomDatabase() {

    abstract fun syncStates(): SyncStateDao

    companion object {

        private const val TAG = "SyncStatesDatabase"

        // For singleton instantiation
        @Volatile
        private var instance: SyncStatesDatabase? = null

        fun getInstance(context: Context): SyncStatesDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): SyncStatesDatabase {
            return Room.databaseBuilder(context.applicationContext, SyncStatesDatabase::class.java, "sync_states.db")
                .fallbackToDestructiveMigration()
                .build()
        }

    }


}
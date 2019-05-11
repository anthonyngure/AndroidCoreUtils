package ke.co.toshngure.basecode.dataloading.sync

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SyncState::class], version = 12, exportSchema = false)
abstract class SyncStatesDatabase : RoomDatabase() {

    abstract fun syncStates(): SyncStateDao

    companion object {

        private const val TAG = "SyncStatesDatabase"


        // For singleton instantiation
        @Volatile
        private lateinit var instance: SyncStatesDatabase

        fun init(context: Context) {
            instance = buildDatabase(context).also { instance = it }
        }

        fun getInstance(): SyncStatesDatabase {
            return instance
        }

        private fun buildDatabase(context: Context): SyncStatesDatabase {
            return Room.databaseBuilder(context.applicationContext, SyncStatesDatabase::class.java, "sync_states.db")
                .fallbackToDestructiveMigration()
                .build()
        }

    }


}
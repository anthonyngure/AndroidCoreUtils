package ke.co.toshngure.basecode.logging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [LogItem::class], version = 1, exportSchema = false)
internal abstract class LogItemsDatabase : RoomDatabase() {

    abstract fun logItems(): LogItemDao

    companion object {

        private const val TAG = "LogItemsDatabase"


        // For singleton instantiation
        @Volatile
        private lateinit var mInstance: LogItemsDatabase

        fun init(context: Context) {
            mInstance = buildDatabase(context).also { mInstance = it }
        }

        fun getInstance(): LogItemsDatabase {
            return mInstance
        }

        private fun buildDatabase(context: Context): LogItemsDatabase {
            return Room.inMemoryDatabaseBuilder(context.applicationContext, LogItemsDatabase::class.java)
                .fallbackToDestructiveMigration()
                .build()
        }

    }


}
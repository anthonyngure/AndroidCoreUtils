package ke.co.toshngure.images.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Image::class], version = 1, exportSchema = false)
abstract class ImagesDatabase : RoomDatabase() {

    abstract fun images(): ImageDao

    companion object {

        private const val TAG = "ImagesDatabase"

        // For singleton instantiation
        @Volatile
        private var instance: ImagesDatabase? = null

        fun getInstance(context: Context): ImagesDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ImagesDatabase {
            return Room.inMemoryDatabaseBuilder(context.applicationContext, ImagesDatabase::class.java)
                    .fallbackToDestructiveMigration()
                    .build()
        }

    }


}
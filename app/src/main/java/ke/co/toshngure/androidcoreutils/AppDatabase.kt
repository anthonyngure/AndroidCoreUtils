package ke.co.toshngure.androidcoreutils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ke.co.toshngure.androidcoreutils.albums.Album
import ke.co.toshngure.androidcoreutils.photos.PhotoDao
import ke.co.toshngure.androidcoreutils.posts.PostDao
import ke.co.toshngure.androidcoreutils.posts.Post
import ke.co.toshngure.androidcoreutils.albums.AlbumDao
import ke.co.toshngure.androidcoreutils.photos.Photo
import ke.co.toshngure.androidcoreutils.users.User
import ke.co.toshngure.androidcoreutils.users.UserDao


@Database(entities = [Post::class, Album::class, Photo::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun posts(): PostDao
    abstract fun albums(): AlbumDao
    abstract fun photos(): PhotoDao
    abstract fun users(): UserDao

    companion object {

        private const val TAG = "AppDatabase"

        // For singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(App.getInstance()).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "data.db")
                    .fallbackToDestructiveMigration()
                    .build()
        }

    }


}
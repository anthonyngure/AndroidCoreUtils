package ke.co.toshngure.androidcoreutils.users

import androidx.paging.DataSource
import androidx.room.RoomDatabase
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.App
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
import retrofit2.Call

class UserRepository : ItemRepository<User, User>() {



    override fun getItemId(item: User): Long {
        return item.id
    }

    override fun getAPICall(before: Long, after: Long): Call<List<User>> {
        return ApiService.getGlamHubInstance().users(before)
    }

    override fun deleteAll() {
        AppDatabase.getInstance().users().deleteAll()
    }


    override fun getItemRepositoryConfig(): ItemRepositoryConfig<User, User> {
        return ItemRepositoryConfig(
            syncClass = User::class.java
        )
    }

    override fun getDatabase(): RoomDatabase {
        return AppDatabase.getInstance()
    }

    override fun getItemDao(): ItemDao<User> {
        return AppDatabase.getInstance().users()
    }

    override fun getItemDataSource(): DataSource.Factory<Int, User> {
        return AppDatabase.getInstance().users().getAllPaged()
    }
}

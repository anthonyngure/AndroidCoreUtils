package ke.co.toshngure.androidcoreutils.users

import android.os.Bundle
import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.api.ApiService
import ke.co.toshngure.androidcoreutils.database.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
import retrofit2.Call

class UserRepository : ItemRepository<User, User>() {


    override fun getItemId(item: User): Long {
        return item.id
    }

    override fun getAPICall(itemAtBottomId: Long, itemAtTopId: Long, args: Bundle?): Call<List<User>> {
        return ApiService.getTypicodeInstance().users(itemAtBottomId)
    }

    override fun deleteAll() {
        AppDatabase.getInstance().users().deleteAll()
    }


    override fun getItemRepositoryConfig(): ItemRepositoryConfig<User> {
        return ItemRepositoryConfig(
            syncClass = User::class.java
        )
    }

    override fun getItemDao(): ItemDao<User> {
        return AppDatabase.getInstance().users()
    }

    override fun getItemDataSource(args: Bundle?): DataSource.Factory<Int, User> {
        return AppDatabase.getInstance().users().getAllPaged()
    }
}

package ke.co.toshngure.androidcoreutils.users

import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import retrofit2.Call

class UserRepository : ItemRepository<User, User>(AppDatabase.getInstance()) {

    override fun getDao(): ItemDao<User> {
        return AppDatabase.getInstance().users()
    }

    override fun index(): DataSource.Factory<Int, User> {
        return  AppDatabase.getInstance().users().getAllPaged()
    }


    override fun getItemId(item: User): Long {
        return item.id
    }

    override fun getAPICall(before: Long, after: Long): Call<List<User>> {
        return ApiService.getGlamHubInstance().users(before)
    }

    override fun deleteAll() {
        AppDatabase.getInstance().users().deleteAll()
    }

    override fun getSyncClass(): Class<User> {
        return User::class.java
    }
}

package ke.co.toshngure.androidcoreutils.albums

import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import retrofit2.Call

class AlbumRepository : ItemRepository<Album, Album>(AppDatabase.getInstance()) {

    override fun getDao(): ItemDao<Album> {
        return AppDatabase.getInstance().albums()
    }


    override fun index(): DataSource.Factory<Int, Album> =
        AppDatabase.getInstance().albums().getAllPaged()

    override fun clear() =
        AppDatabase.getInstance().albums().deleteAll()

    override fun getCall(before: Long, after: Long): Call<List<Album>>? {
        return ApiService.getInstance().albums(after)
    }


    override fun getItemId(item: Album): Long {
        return item.id
    }
}
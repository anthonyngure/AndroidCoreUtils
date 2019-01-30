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

    override fun deleteAll() =
        AppDatabase.getInstance().albums().deleteAll()

    override fun getAPICall(before: Long, after: Long): Call<List<Album>> {
        return ApiService.getTypicodeInstance().albums(before)
    }

    override fun getItemId(item: Album): Long {
        return item.id
    }

    override fun getSyncClass(): Class<Album> {
        return Album::class.java
    }
}
package ke.co.toshngure.androidcoreutils.albums

import android.os.Bundle
import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.api.ApiService
import ke.co.toshngure.androidcoreutils.database.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
import retrofit2.Call

class AlbumRepository : ItemRepository<Album, Album>() {

    override fun deleteAll() {
        return AppDatabase.getInstance().albums().deleteAll()
    }

    override fun getAPICall(before: Long, after: Long): Call<List<Album>> {
        return ApiService.getTypicodeInstance().albums(before)
    }

    override fun getRefreshAPICall(): Call<List<Album>>? {
        return ApiService.getTypicodeInstance().albums()
    }

    override fun getItemId(item: Album): Long {
        return item.id
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Album, Album> {
        return ItemRepositoryConfig(
            syncClass = Album::class.java
        )
    }


    override fun getItemDataSource(args: Bundle?): DataSource.Factory<Int, Album> {
        return AppDatabase.getInstance().albums().getAllPaged()
    }

    override fun getItemDao(): ItemDao<Album> {
        return AppDatabase.getInstance().albums()
    }
}
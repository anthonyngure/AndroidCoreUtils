package ke.co.toshngure.androidcoreutils.albums

import android.os.Bundle
import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.api.ApiService
import ke.co.toshngure.androidcoreutils.database.AppDatabase
import ke.co.toshngure.basecode.paging.data.ItemDao
import ke.co.toshngure.basecode.paging.data.ItemRepository
import ke.co.toshngure.basecode.paging.data.ItemRepositoryConfig
import retrofit2.Call

class AlbumRepository : ItemRepository<Album, Album>() {

    override fun deleteAll() {
        return AppDatabase.getInstance().albums().deleteAll()
    }

    override fun getAPICall(itemAtBottomId: Long, itemAtTopId: Long, args: Bundle?): Call<List<Album>> {
        return ApiService.getTypicodeInstance().albums(itemAtBottomId)
    }

    override fun getRefreshAPICall(args: Bundle?): Call<List<Album>>? {
        return ApiService.getTypicodeInstance().albums()
    }

    override fun getItemId(item: Album): Long {
        return item.id
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Album> {
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
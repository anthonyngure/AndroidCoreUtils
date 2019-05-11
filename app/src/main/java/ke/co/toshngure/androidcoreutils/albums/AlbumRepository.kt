package ke.co.toshngure.androidcoreutils.albums

import androidx.paging.DataSource
import androidx.room.RoomDatabase
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
import ke.co.toshngure.images.data.ImagesDatabase
import retrofit2.Call

class AlbumRepository : ItemRepository<Album, Album>() {


    override fun deleteAll() =
        AppDatabase.getInstance().albums().deleteAll()

    override fun getAPICall(before: Long, after: Long): Call<List<Album>> {
        return ApiService.getTypicodeInstance().albums(before)
    }

    override fun getItemId(item: Album): Long {
        return item.id
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Album, Album> {
        return ItemRepositoryConfig(
            syncClass = Album::class.java
        )
    }


    override fun getItemDataSource(): DataSource.Factory<Int, Album> {
        return AppDatabase.getInstance().albums().getAllPaged()
    }

    override fun getItemDao(): ItemDao<Album> {
        return AppDatabase.getInstance().albums()
    }
}
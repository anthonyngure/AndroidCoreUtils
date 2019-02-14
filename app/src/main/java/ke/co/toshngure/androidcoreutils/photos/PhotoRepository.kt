package ke.co.toshngure.androidcoreutils.photos

import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.androidcoreutils.albums.Album
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
import retrofit2.Call

class PhotoRepository(private val albumId: Long) : ItemRepository<Photo, Photo>() {


    override fun getItemId(item: Photo): Long {
        return item.id
    }

    override fun getAPICall(before: Long, after: Long): Call<List<Photo>> {
        return ApiService.getTypicodeInstance().photos(albumId, before)
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Photo,Photo> {
        return ItemRepositoryConfig(
            syncClass = Photo::class.java,
            db = AppDatabase.getInstance()
        )
    }

    override fun getItemDao(): ItemDao<Photo> {
        return AppDatabase.getInstance().photos()
    }

    override fun getItemDataSource(): DataSource.Factory<Int, Photo> {
        return AppDatabase.getInstance().photos().getAllPaged(albumId)
    }
}

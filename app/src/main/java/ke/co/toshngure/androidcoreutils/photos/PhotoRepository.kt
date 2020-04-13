package ke.co.toshngure.androidcoreutils.photos

import android.os.Bundle
import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.api.ApiService
import ke.co.toshngure.androidcoreutils.database.AppDatabase
import ke.co.toshngure.basecode.paging.data.ItemDao
import ke.co.toshngure.basecode.paging.data.ItemRepository
import ke.co.toshngure.basecode.paging.data.ItemRepositoryConfig
import retrofit2.Call

class PhotoRepository(private val albumId: Long) : ItemRepository<Photo, Photo>() {


    override fun save(items: List<Photo>, args: Bundle?) {
        for (item in items) {
            item.albumId = albumId
        }
        super.save(items, args)
    }


    override fun getItemId(item: Photo): Long {
        return item.id
    }

    override fun getAPICall(itemAtBottomId: Long, itemAtTopId: Long, args: Bundle?): Call<List<Photo>> {
        return ApiService.getTypicodeInstance().photos(albumId, itemAtBottomId)
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Photo> {
        return ItemRepositoryConfig(
            syncClass = Photo::class.java
        )
    }


    override fun getItemDao(): ItemDao<Photo> {
        return AppDatabase.getInstance().photos()
    }

    override fun getItemDataSource(args: Bundle?): DataSource.Factory<Int, Photo> {
        return AppDatabase.getInstance().photos().getAllPaged(albumId)
    }
}

package ke.co.toshngure.androidcoreutils.photos

import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import retrofit2.Call

class PhotoRepository(private val albumId: Long) : ItemRepository<Photo, Photo>(AppDatabase.getInstance()) {

    override fun getDao(): ItemDao<Photo> {
        return AppDatabase.getInstance().photos()
    }

    override fun index(): DataSource.Factory<Int, Photo> =
        AppDatabase.getInstance().photos().getAllPaged(albumId)


    override fun getItemId(item: Photo): Long {
        return item.id
    }

    override fun getAPICall(before: Long, after: Long): Call<List<Photo>> {
        return ApiService.getTypicodeInstance().photos(albumId, before)
    }

    override fun getSyncClass(): Class<Photo> {
        return Photo::class.java
    }
}

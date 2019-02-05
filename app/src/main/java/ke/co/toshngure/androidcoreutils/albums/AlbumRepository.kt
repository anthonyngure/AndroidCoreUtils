package ke.co.toshngure.androidcoreutils.albums

import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
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
            syncClass = Album::class.java,
            itemDao = AppDatabase.getInstance().albums(),
            db = AppDatabase.getInstance(),
            dataSourceFactory = AppDatabase.getInstance().albums().getAllPaged()
        )
    }
}
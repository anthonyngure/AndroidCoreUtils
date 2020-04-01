package ke.co.toshngure.androidcoreutils.posts

import android.os.Bundle
import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.api.ApiService
import ke.co.toshngure.androidcoreutils.database.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
import retrofit2.Call

class PostRepository : ItemRepository<Post, Post>() {

    override fun getItemId(item: Post): Long {
        return item.id
    }

    override fun getAPICall(before: Long, after: Long, args: Bundle?): Call<List<Post>> {
        return ApiService.getTypicodeInstance().posts(before)
    }

    override fun getRefreshAPICall(args: Bundle?): Call<List<Post>>? {
        return ApiService.getTypicodeInstance().posts()
    }

    override fun deleteAll() {
        AppDatabase.getInstance().posts().deleteAll()
    }


    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Post, Post> {
        return ItemRepositoryConfig(
            syncClass = Post::class.java
        )
    }

    override fun getItemDao(): ItemDao<Post> {
        return AppDatabase.getInstance().posts()
    }

    override fun getItemDataSource(args: Bundle?): DataSource.Factory<Int, Post> {
        return AppDatabase.getInstance().posts().getAllPaged()
    }
}

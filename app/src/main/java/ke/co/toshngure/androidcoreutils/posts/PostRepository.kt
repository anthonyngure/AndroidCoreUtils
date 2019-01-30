package ke.co.toshngure.androidcoreutils.posts

import androidx.paging.DataSource
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import retrofit2.Call

class PostRepository() : ItemRepository<Post, Post>(AppDatabase.getInstance()) {

    override fun getDao(): ItemDao<Post> {
        return AppDatabase.getInstance().posts()
    }

    override fun index(): DataSource.Factory<Int, Post> {
        return  AppDatabase.getInstance().posts().getAllPaged()
    }


    override fun getItemId(item: Post): Long {
        return item.id
    }

    override fun getAPICall(before: Long, after: Long): Call<List<Post>> {
        return ApiService.getTypicodeInstance().posts(before)
    }

    override fun deleteAll() {
        AppDatabase.getInstance().posts().deleteAll()
    }

    override fun getSyncClass(): Class<Post> {
        return Post::class.java
    }
}

package ke.co.toshngure.androidcoreutils.posts

import androidx.paging.DataSource
import androidx.room.*
import ke.co.toshngure.basecode.dataloading.data.ItemDao

@Dao
interface PostDao : ItemDao<Post> {

    @Query("SELECT * FROM posts ORDER BY update_time DESC")
    fun getAllPaged(): DataSource.Factory<Int, Post>

    @Query("DELETE FROM posts")
    fun deleteAll()
}
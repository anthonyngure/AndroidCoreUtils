package ke.co.toshngure.androidcoreutils.posts

import androidx.paging.DataSource
import androidx.room.*
import ke.co.toshngure.basecode.paging.data.ItemDao

@Dao
interface PostDao : ItemDao<Post> {

    @Query("SELECT * FROM posts ORDER BY id ASC")
    fun getAllPaged(): DataSource.Factory<Int, Post>

    @Query("DELETE FROM posts")
    fun deleteAll()
}
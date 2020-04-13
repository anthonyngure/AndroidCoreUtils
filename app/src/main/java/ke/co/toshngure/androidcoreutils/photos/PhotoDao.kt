package ke.co.toshngure.androidcoreutils.photos

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import ke.co.toshngure.basecode.paging.data.ItemDao

@Dao
interface PhotoDao : ItemDao<Photo> {

    @Query("SELECT * FROM photos WHERE album_id = :albumId ORDER BY id ASC")
    fun getAllPaged(albumId: Long): DataSource.Factory<Int, Photo>

    @Query("DELETE FROM PHOTOS WHERE album_id = :albumId")
    fun deleteAll(albumId: Long)
}
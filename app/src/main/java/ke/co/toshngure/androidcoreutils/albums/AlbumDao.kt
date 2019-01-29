package ke.co.toshngure.androidcoreutils.albums

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import ke.co.toshngure.basecode.dataloading.data.ItemDao

@Dao
interface AlbumDao : ItemDao<Album> {

    @Query("SELECT * FROM albums")
    fun getAllPaged(): DataSource.Factory<Int, Album>

    @Query("DELETE FROM albums")
    fun deleteAll()
}
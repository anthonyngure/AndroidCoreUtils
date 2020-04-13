package ke.co.toshngure.images.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import ke.co.toshngure.basecode.paging.data.ItemDao

@Dao
interface ImageDao : ItemDao<Image> {

    @Query("SELECT * FROM images ORDER BY dateAdded DESC")
    fun getAllPaged(): DataSource.Factory<Int, Image>

    @Query("SELECT * FROM images WHERE bucket = :bucket ORDER BY dateAdded DESC")
    fun getAllPagedByFolder(bucket: String): DataSource.Factory<Int, Image>

    @Query("SELECT DISTINCT bucket FROM images")
    fun getFolderList(): LiveData<List<String>>

    @Query("SELECT * FROM images WHERE selected = 1 ORDER BY selectedAt ASC")
    fun getAllSelected(): LiveData<List<Image>>

    @Query("UPDATE images SET selected = 0 WHERE selected = 1")
    fun clearAllSelection()

    @Query("DELETE FROM images")
    fun deleteAll()
}
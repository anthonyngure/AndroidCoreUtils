package ke.co.toshngure.basecode.logging

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import ke.co.toshngure.basecode.paging.data.ItemDao

@Dao
interface LogItemDao : ItemDao<LogItem> {

    @Query("SELECT * FROM log_items ORDER BY id DESC")
    fun getAllPaged(): DataSource.Factory<Int, LogItem>

    @Query("SELECT * FROM log_items WHERE title = :subTag ORDER BY id DESC")
    fun getAllBySubTagPaged(subTag: String): DataSource.Factory<Int, LogItem>

    @Query("SELECT DISTINCT title FROM log_items ORDER BY title DESC")
    fun getSubTagList(): LiveData<List<String>>

    @Query("DELETE FROM log_items")
    fun deleteAll()
}
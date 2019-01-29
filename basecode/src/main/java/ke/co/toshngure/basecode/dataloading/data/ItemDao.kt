package ke.co.toshngure.basecode.dataloading.data

import androidx.room.*


interface ItemDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: T)

    @Delete
    fun delete(item: T)
}
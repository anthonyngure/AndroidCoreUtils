package ke.co.toshngure.basecode.dataloading.data

import androidx.room.*


interface ItemDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: T)

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: List<T>)

    @Delete
    fun delete(item: T)

    @Transaction
    @Delete
    fun delete(item: List<T>)

}
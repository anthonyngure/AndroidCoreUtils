package ke.co.toshngure.androidcoreutils.users

import androidx.paging.DataSource
import androidx.room.*
import ke.co.toshngure.basecode.paging.data.ItemDao

@Dao
interface UserDao : ItemDao<User> {

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllPaged(): DataSource.Factory<Int, User>

    @Query("DELETE FROM users")
    fun deleteAll()
}
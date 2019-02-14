package ke.co.toshngure.basecode.dataloading.data

import androidx.room.RoomDatabase

data class ItemRepositoryConfig<Model, LoadedModel>(
        val paginates: Boolean = true,
        val connects: Boolean = true,
        val networkPerPage: Int = NETWORK_PER_PAGE,
        val dbPerPage: Int = DB_PER_PAGE,
        val ordersDesc: Boolean = true,
        val syncClass: Class<Model>,
        //val itemDao: ItemDao<Model>,
        //val dataSourceFactory: DataSource.Factory<Int, LoadedModel>,
        val db: RoomDatabase
) {


    companion object {

        const val NETWORK_PER_PAGE = 10
        const val DB_PER_PAGE = 10
    }
}
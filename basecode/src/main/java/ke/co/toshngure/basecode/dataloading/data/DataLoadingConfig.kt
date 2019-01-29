package ke.co.toshngure.basecode.dataloading.data

data class DataLoadingConfig(val paginates: Boolean = true,
                             val networkPerPage: Int = NETWORK_PER_PAGE,
                             val dbPerPage: Int = DB_PER_PAGE,
                             val ordersDesc: Boolean = true) {


    companion object {

        const val NETWORK_PER_PAGE = 10
        const val DB_PER_PAGE = 10
    }
}
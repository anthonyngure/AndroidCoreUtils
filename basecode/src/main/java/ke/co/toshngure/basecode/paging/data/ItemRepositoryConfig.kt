package ke.co.toshngure.basecode.paging.data

data class ItemRepositoryConfig<Model>(
        val networkPerPage: Int = NETWORK_PER_PAGE,
        val dbPerPage: Int = DB_PER_PAGE,
        val ordersDesc: Boolean = true,
        val syncClass: Class<Model>
) {


    companion object {

        const val NETWORK_PER_PAGE = 10
        const val DB_PER_PAGE = 10
    }
}
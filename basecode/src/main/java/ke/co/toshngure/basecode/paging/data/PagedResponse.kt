package ke.co.toshngure.basecode.paging.data

/**
 * Created by Anthony Ngure on 4/12/2020
 *
 * @author Anthony Ngure
 */
data class PagedResponse<Model>(
    val currentPage: Int,
    val data: List<Model>,
    val firstPageUrl: String?,
    val from: Int,
    val lastPage: Int,
    val lastPageUrl: String?,
    val nextPageUrl: String?,
    val path: String?,
    val perPage: Int,
    val prevPageUrl: Int,
    val to: String?,
    val total: Int,
    val sortBy: String?,
    val sortDirection: String?,
    val filter: String?
)
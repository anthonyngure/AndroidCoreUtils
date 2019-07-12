package ke.co.toshngure.androidcoreutils

import ke.co.toshngure.androidcoreutils.albums.Album
import ke.co.toshngure.androidcoreutils.photos.Photo
import ke.co.toshngure.androidcoreutils.posts.Post
import ke.co.toshngure.androidcoreutils.users.User
import ke.co.toshngure.basecode.util.NetworkUtils
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("dataTest")
    fun users(
        @Query("before") before: Long = 0,
        @Query("after") after: Long = 0,
        @Query("perPage") perPage: Int = 10,
        @QueryMap params: Map<String, String> = mapOf()
    ): Call<List<User>>

    @GET("/posts")
    fun posts(
        @Query("_start") start: Long,
        @Query("_limit") perPage: Int = 10,
        @Query("_order") order: String = "desc",
        @QueryMap params: Map<String, String> = mapOf()
    ): Call<List<Post>>

    @GET("/albums")
    fun albums(
        @Query("_start") start: Long,
        @Query("_limit") perPage: Int = 10,
        @Query("_order") order: String = "desc",
        @QueryMap params: Map<String, String> = mapOf()
    ): Call<List<Album>>


    @GET("/albums/{albumId}/photos")
    fun photos(
        @Path("albumId") albumId: Long,
        @Query("_start") start: Long,
        @Query("_limit") perPage: Int = 10,
        @Query("_order") order: String = "desc",
        @QueryMap params: Map<String, String> = mapOf()
    ): Call<List<Photo>>


    companion object {

        private const val TAG = "ApiService"
        private const val BASE_URL = "https://jsonplaceholder.typicode.com"
        private const val GLAMHUB_BASE_URL = "http://dev-api.glamhub.co.ke/api/v1/"

        // For singleton instantiation
        @Volatile
        private var instance: ApiService? = null

        @Volatile
        private var glamhub: ApiService? = null

        fun getTypicodeInstance(): ApiService {
            return instance ?: synchronized(this) {
                instance ?: buildInstance(BASE_URL).also { instance = it }
            }
        }

        fun getGlamHubInstance(): ApiService {
            return glamhub ?: synchronized(this) {
                glamhub ?: buildInstance(GLAMHUB_BASE_URL).also { glamhub = it }
            }
        }

        private fun buildInstance(baseUrl: String): ApiService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkUtils.getClientInstance(App.getInstance()))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
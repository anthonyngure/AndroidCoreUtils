package ke.co.toshngure.androidcoreutils.api

import ke.co.toshngure.androidcoreutils.albums.Album
import ke.co.toshngure.androidcoreutils.photos.Photo
import ke.co.toshngure.androidcoreutils.posts.Post
import ke.co.toshngure.androidcoreutils.users.User
import ke.co.toshngure.basecode.net.NetworkUtil
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("/users")
    fun users(
        @Query("_start") start: Long = 0,
        @Query("_limit") perPage: Int = 10,
        @Query("_order") order: String = "asc",
        @QueryMap params: Map<String, String> = mapOf()
    ): Call<List<User>>

    @GET("/posts")
    fun posts(
        @Query("_start") start: Long = 0,
        @Query("_limit") perPage: Int = 10,
        @Query("_order") order: String = "desc",
        @QueryMap params: Map<String, String> = mapOf()
    ): Call<List<Post>>

    @GET("/posts/{postId}")
    fun post(@Path("postId") postId: Long): Call<Post>


    @GET("/albums")
    fun albums(
        @Query("_start") start: Long = 0,
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

        @Suppress("unused")
        private const val TAG = "ApiService"
        private const val BASE_URL = "https://jsonplaceholder.typicode.com"

        // For singleton instantiation
        @Volatile
        private var instance: ApiService? = null

        fun getTypicodeInstance(): ApiService {
            return instance ?: synchronized(this) {
                instance ?: buildInstance().also { instance = it }
            }
        }

        private fun buildInstance(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(NetworkUtil.getClientInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
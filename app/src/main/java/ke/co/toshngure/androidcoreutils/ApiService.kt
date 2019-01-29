package ke.co.toshngure.androidcoreutils

import ke.co.toshngure.androidcoreutils.photos.Photo
import ke.co.toshngure.androidcoreutils.posts.Post
import ke.co.toshngure.basecode.util.NetworkUtils
import ke.co.toshngure.androidcoreutils.albums.Album
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("/posts")
    fun posts(
        @Query("_start") start: Long,
        @Query("_limit") perPage: Int = 10,
        @QueryMap params: Map<String, String> = mapOf()
    ): Call<List<Post>>

    @GET("/albums")
    fun albums(@Query("_start") start: Long,
               @Query("_limit") perPage: Int = 10,
               @QueryMap params: Map<String, String> = mapOf()): Call<List<Album>>


    @GET("/albums/{albumId}/photos")
    fun photos(@Path("albumId") albumId: Long,
               @Query("_start") start: Long,
               @Query("_limit") perPage: Int = 10,
               @QueryMap params: Map<String, String> = mapOf()): Call<List<Photo>>


    companion object {

        private const val TAG = "ApiService"
        private const val BASE_URL = "https://jsonplaceholder.typicode.com"

        // For singleton instantiation
        @Volatile
        private var instance: ApiService? = null

        fun getInstance(): ApiService {
            return instance ?: synchronized(this) {
                instance ?: buildInstance().also { instance = it }
            }
        }

        private fun buildInstance(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(NetworkUtils.getClientInstance())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
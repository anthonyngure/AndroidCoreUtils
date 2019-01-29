package ke.co.toshngure.basecode.util

import android.content.Context
import android.net.ConnectivityManager
import ke.co.toshngure.basecode.logging.BeeLog
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object NetworkUtils {

    private var mClientInstance: OkHttpClient? = null

    fun getClientInstance(): OkHttpClient {
        return mClientInstance ?: synchronized(this) {
            mClientInstance ?: buildClient().also { mClientInstance = it }
        }
    }

    private fun buildClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            BeeLog.i("OkHttpClient", it)
        })
        logger.level = HttpLoggingInterceptor.Level.BODY

        // The following line of code specifies a cache of 200MB.
        val cacheSize = (200 * 1024 * 1024).toLong()

        // We pass in the cache directory and the cache size as parameters to create the Cache variable for our OkHttpClient
       // val myCache = Cache(App.getInstance().cacheDir, cacheSize)

        return OkHttpClient.Builder()

                .addInterceptor(logger)
                //.addInterceptor(ChuckInterceptor(App.getInstance()))

                /*// Specify the cache we created earlier.
                .cache(myCache)

                // Add an Interceptor to the OkHttpClient.
                .addInterceptor { chain ->

                    val original = chain.request()

                    // Get the request from the chain.
                    val request = original.newBuilder()
                            .method(original.method(), original.body())
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer ${PrefUtils.getInstance().getUser()?.token}")


                    *//*
                    *  Leveraging the advantage of using Kotlin,
                    *  we initialize the request and change its header depending on whether
                    *  the device is connected to Internet or not.
                    *//*
                    if (BaseUtils.canConnect(App.getInstance()))
                    *//*
                    *  If there is Internet, get the cache that was stored 5 seconds ago.
                    *  If the cache is older than 5 seconds, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-age' attribute is responsible for this behavior.
                    *//*
                        request.header("Cache-Control", "public, max-age=" + 60000).build()
                    else
                    *//*
                    *  If there is no Internet, get the cache that was stored 7 days ago.
                    *  If the cache is older than 7 days, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-stale' attribute is responsible for this behavior.
                    *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                    *//*
                        request.header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                    // End of if-else statement


                    // Add the modified request to the chain.
                    val response = chain.proceed(request.build())


                    if (response.code() == 401) {
                        mClientInstance?.dispatcher()?.cancelAll()
                        PrefUtils.getInstance().signOut()
                        //App.getInstance().startActivity(Intent(App.getInstance(), ReAuthActivity::class.java))
                    }
                    response
                }*/

                .build()
    }

    fun canConnect(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
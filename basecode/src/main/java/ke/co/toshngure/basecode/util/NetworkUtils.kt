package ke.co.toshngure.basecode.util

import android.content.Context
import android.net.ConnectivityManager
import com.readystatesoftware.chuck.ChuckInterceptor
import ke.co.toshngure.basecode.logging.BeeLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object NetworkUtils {

    interface Callback {
        fun onAuthError(statusCode: Int)
        fun getAuthToken() : String?
    }

    private var mClientInstance: OkHttpClient? = null
    private var mCallback: Callback? = null

    fun getClientInstance(callback: Callback? = null): OkHttpClient {
        if (callback != mCallback){
            mClientInstance = null
        }
        return mClientInstance ?: synchronized(this) {
            mClientInstance ?: buildClient(callback).also { mClientInstance = it }
        }
    }

    private fun buildClient(callback: Callback? = null): OkHttpClient {
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            BeeLog.i("OkHttpClient", it)
        })
        logger.level = HttpLoggingInterceptor.Level.BODY


        return OkHttpClient.Builder()

            .addInterceptor(logger)

            // Add an Interceptor to the OkHttpClient.
            .addInterceptor { chain ->

                val original = chain.request()

                // Get the request from the chain.
                val request = original.newBuilder()
                    .method(original.method(), original.body())
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer ${callback?.getAuthToken()}")

                // Add the modified request to the chain.
                val response = chain.proceed(request.build())


                if (response.code() == 401 || response.code() == 403) {
                    callback?.onAuthError(response.code())
                    mClientInstance?.dispatcher()?.cancelAll()
                }

                response
            }

            .build()
    }

    fun canConnect(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
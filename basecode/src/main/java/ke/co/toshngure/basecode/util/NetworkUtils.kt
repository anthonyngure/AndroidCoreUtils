package ke.co.toshngure.basecode.util

import android.content.Context
import android.net.ConnectivityManager
import ke.co.toshngure.basecode.logging.BeeLog
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor

class NetworkUtils private constructor() {


    companion object {

        private lateinit var mCallback: Callback
        private lateinit var mInstance: NetworkUtils
        private var mClientInstance: OkHttpClient? = null

        fun init(callback: Callback) {
            mInstance = NetworkUtils()
            mCallback = callback
        }

        fun getClientInstance(): OkHttpClient {
            return mClientInstance ?: synchronized(this) {
                mClientInstance ?: buildClient().also { mClientInstance = it }
            }
        }

        fun getInstance(): NetworkUtils {
            return mInstance
        }

        fun getCallback(): Callback {
            return mCallback
        }

        private fun buildClient(): OkHttpClient {

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
                                .header("Authorization", "Bearer ${mCallback.getAuthToken()}")

                        // Add the modified request to the chain.
                        val response = chain.proceed(request.build())


                        if (response.code() == 401 || response.code() == 403) {
                            mCallback.onAuthError(response.code())
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

    interface Callback {
        fun onAuthError(statusCode: Int)
        fun getAuthToken(): String?
        fun getErrorMessageFromResponseBody(statusCode: Int, responseBody: ResponseBody): String
    }


}
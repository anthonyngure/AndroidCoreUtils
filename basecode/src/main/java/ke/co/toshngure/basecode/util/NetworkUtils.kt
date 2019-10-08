package ke.co.toshngure.basecode.util

import android.content.Context
import android.net.ConnectivityManager
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import ke.co.toshngure.basecode.logging.BeeLog
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.internal.platform.Platform
import java.util.concurrent.TimeUnit


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


            // .BASIC)
            // .NONE // No logs
            //   .BASIC // Logging url,method,headers and body.
            //   .HEADERS // Logging headers
            //    .BODY // Logging body


            val builder = OkHttpClient.Builder()

                .hostnameVerifier { _, _ -> true }

                .connectTimeout(mCallback.getConnectTimeoutInSeconds(), TimeUnit.SECONDS)
                .writeTimeout(mCallback.getWriteTimeoutInSeconds(), TimeUnit.SECONDS)
                .readTimeout(mCallback.getReadTimeoutInSeconds(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .followRedirects(false)


            // Add an Interceptor to the OkHttpClient.
            builder.addInterceptor { chain ->

                // Get the request from the chain.
                val original = chain.request()

                // Get url
                val url = original.url().newBuilder()
                // Get common params
                val commonParams = mCallback.getCommonParams()
                // Add common params to the url
                for (key in commonParams.keys) {
                    url.addQueryParameter(key, commonParams[key].toString())
                }

                val request = original.newBuilder()
                    .url(url.build())
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

            builder.addInterceptor(ChuckInterceptor(mCallback.getContext()))

            builder.addInterceptor(
                LoggingInterceptor.Builder()
                    .loggable(BeeLog.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("OkHttpClientRequest")
                    .response("OkHttpClientResponse")
                    //.addHeader("version", BuildConfig.VERSION_NAME)
                    //.addQueryParam("query", "0")
                    .enableAndroidStudio_v3_LogsHack(true) /* enable fix for logCat logging issues with pretty format */
                    //              .logger(new Logger() {
                    //                  @Override
                    //                  public void log(int level, String tag, String msg) {
                    //                      Log.w(tag, msg);
                    //                  }
                    //              })
                    //              .executor(Executors.newSingleThreadExecutor())
                    .build()
            )
            return builder.build()
        }

        @Suppress("unused")
        fun canConnect(context: Context): Boolean {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    }

    interface Callback {
        fun onAuthError(statusCode: Int)
        fun getAuthToken(): String?
        fun getErrorMessageFromResponseBody(statusCode: Int, responseBody: ResponseBody?): String
        fun getContext(): Context
        fun getCommonParams(): Map<String, Any>
        fun getReadTimeoutInSeconds(): Long {
            return 60
        }

        fun getWriteTimeoutInSeconds(): Long {
            return 60
        }

        fun getConnectTimeoutInSeconds(): Long {
            return 30
        }
    }


}
package ke.co.toshngure.basecode.net

import android.content.Context
import android.net.ConnectivityManager
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.app.LoadingConfig
import ke.co.toshngure.basecode.logging.BeeLog
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import java.util.concurrent.TimeUnit


class NetworkUtil private constructor() {


    companion object {

        private lateinit var mCallback: Callback
        private lateinit var mInstance: NetworkUtil
        private var mClientInstance: OkHttpClient? = null

        fun init(callback: Callback) {
            mInstance =
                NetworkUtil()
            mCallback = callback
        }

        fun getClientInstance(): OkHttpClient {
            return mClientInstance ?: synchronized(this) {
                mClientInstance
                    ?: buildClient().also { mClientInstance = it }
            }
        }

        fun getInstance(): NetworkUtil {
            return mInstance
        }

        fun getCallback(): Callback {
            return mCallback
        }

        private fun buildClient(): OkHttpClient {

            val cacheSize = (5 * 1024 * 1024).toLong()
            val appCache = Cache(mCallback.getContext().cacheDir, cacheSize)

            val builder = OkHttpClient.Builder()

                .cache(appCache)

                .hostnameVerifier { _, _ -> true }

                .connectTimeout(mCallback.getConnectTimeoutInSeconds(), TimeUnit.SECONDS)
                .writeTimeout(mCallback.getWriteTimeoutInSeconds(), TimeUnit.SECONDS)
                .readTimeout(mCallback.getReadTimeoutInSeconds(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .followRedirects(false)

            // Add common params interceptor
            builder.addInterceptor(CommonParamsInterceptor(mCallback))

            // Add common headers
            builder.addInterceptor(CommonHeadersInterceptor(mCallback))

            // Add chuck
            if (BeeLog.DEBUG) {
                builder.addInterceptor(ChuckInterceptor(mCallback.getContext()))
            }

            // Add logging interceptor
            builder.addInterceptor(
                LoggingInterceptor.Builder()
                    .loggable(BeeLog.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("OkHttpClientRequest")
                    .response("OkHttpClientResponse")
                    //.addHeader("version", BuildConfig.VERSION_NAME)
                    //.addQueryParam("query", "0")
                    /* enable fix for logCat logging issues with pretty format */
                    .enableAndroidStudio_v3_LogsHack(true)
                    .build()
            )

            // Add cache interceptor
            builder.addInterceptor(CacheInterceptor(mCallback))

            // Add authorization interceptor
            builder.addInterceptor(AuthorizationInterceptor(mClientInstance, mCallback))

            return builder.build()
        }

        @Suppress("DEPRECATION")
        fun canConnect(context: Context): Boolean {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    }

    interface Callback {


        fun getContext(): Context

        fun getErrorMessageFromResponseBody(statusCode: Int, errorResponseBody: String?): String {
            return getContext().getString(R.string.message_connection_error)
        }

        fun onAuthError(statusCode: Int) {

        }

        fun getAuthToken(): String? {
            return null
        }

        fun getDefaultLoadingConfig(): LoadingConfig {
            return LoadingConfig(
                refreshEnabled = false,
                showNoDataLayout = true,
                showLoading = true,
                showErrorDialog = true,
                withLoadingLayoutAtTop = false,
                withNoDataLayoutAtTop = false,
                withErrorLayoutAtTop = false,
                loadingMessage = R.string.message_waiting,
                noDataMessage = R.string.message_empty_data,
                noDataIcon = R.drawable.ic_cloud_queue_black_24dp,
                errorIcon = R.drawable.ic_cloud_off_black_24dp
            )
        }


        fun getCommonParams(): Map<String, Any> {
            return mapOf()
        }

        fun getCommonHeaders(): Map<String, String> {
            return mapOf()
        }

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
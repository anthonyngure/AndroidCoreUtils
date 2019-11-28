package ke.co.toshngure.androidcoreutils

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.net.NetworkUtils
import okhttp3.ResponseBody

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        mInstance = this
        NetworkUtils.init(NetworkUtilsCallback())
        SyncStatesDatabase.init(this)
        BeeLog.init(this, TAG, BuildConfig.DEBUG)
        Stetho.initializeWithDefaults(this)

    }

    companion object {
        private const val TAG = "PagingWithRoom"

        // For singleton instantiation
        @Volatile
        private lateinit var mInstance: App

        fun getInstance(): App = mInstance

        class NetworkUtilsCallback : NetworkUtils.Callback {
            override fun getCommonParams(): Map<String, String> {
                return mapOf()
            }

            override fun getErrorMessageFromResponseBody(
                statusCode: Int,
                responseBody: ResponseBody?
            ): String {
                BeeLog.i(TAG, responseBody)
                return responseBody?.string()
                    ?: mInstance.getString(R.string.message_connection_error)
            }

            override fun getContext(): Context {
                return mInstance
            }


            override fun onAuthError(statusCode: Int) {

            }

            override fun getAuthToken(): String? {
                return null
            }

        }
    }
}
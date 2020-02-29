package ke.co.toshngure.androidcoreutils

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.net.NetworkUtils
import ke.co.toshngure.basecode.util.PrefUtils
import okhttp3.ResponseBody

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        mInstance = this
        PrefUtils.init(this)
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

            override fun getErrorMessageFromResponseBody(
                statusCode: Int,
                errorResponseBody: String?
            ): String {
                BeeLog.i(TAG, errorResponseBody)
                return errorResponseBody
                    ?: mInstance.getString(R.string.message_connection_error)
            }

            override fun getContext(): Context {
                return mInstance
            }

        }
    }
}
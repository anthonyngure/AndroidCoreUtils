package ke.co.toshngure.androidcoreutils

import android.app.Application
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.util.NetworkUtils
import okhttp3.ResponseBody

class App : Application(), NetworkUtils.Callback {
    override fun onAuthError(statusCode: Int) {

    }

    override fun getAuthToken(): String? {
        return null
    }

    override fun getErrorMessageFromResponseBody(statusCode: Int, responseBody: ResponseBody): String {
        return responseBody.string()
    }

    companion object {
        private const val TAG = "PagingWithRoom"

        // For singleton instantiation
        @Volatile
        private lateinit var instance: App

        fun getInstance(): App = instance
    }

    override fun onCreate() {
        super.onCreate()
        NetworkUtils.init(this)
        SyncStatesDatabase.init(this)
        instance = this
        BeeLog.init(BuildConfig.DEBUG, TAG)
    }
}
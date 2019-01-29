package ke.co.toshngure.androidcoreutils

import android.app.Application
import ke.co.toshngure.basecode.logging.BeeLog

class App : Application() {

    companion object {
        private const val TAG = "PagingWithRoom"

        // For singleton instantiation
        @Volatile
        private lateinit var instance: App

        fun getInstance(): App = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        BeeLog.init(BuildConfig.DEBUG, TAG)
    }
}
package ke.co.toshngure.androidcoreutils

import android.app.Application
import com.facebook.stetho.Stetho
import ke.co.toshngure.androidcoreutils.api.NetworkUtilCallback
import ke.co.toshngure.basecode.paging.sync.SyncStatesDatabase
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.net.NetworkUtil
import ke.co.toshngure.basecode.util.PrefUtils

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        mInstance = this

        PrefUtils.init(this)

        NetworkUtil.init(NetworkUtilCallback())

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
    }
}
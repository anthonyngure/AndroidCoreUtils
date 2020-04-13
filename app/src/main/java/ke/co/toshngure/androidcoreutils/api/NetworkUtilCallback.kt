package ke.co.toshngure.androidcoreutils.api

import android.content.Context
import ke.co.toshngure.androidcoreutils.App
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.net.NetworkUtil

class NetworkUtilCallback : NetworkUtil.Callback {

    override fun getErrorMessageFromResponseBody(
        statusCode: Int,
        errorResponseBody: String?
    ): String {
        BeeLog.i(TAG, errorResponseBody)
        return errorResponseBody
            ?: App.getInstance().getString(R.string.message_connection_error)
    }

    override fun getContext(): Context {
        return App.getInstance()
    }

    companion object {
        private const val TAG = "NetworkUtilsCallback"
    }

}
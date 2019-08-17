package ke.co.toshngure.basecode.smsretriever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import ke.co.toshngure.basecode.logging.BeeLog

/**
 * Created by Anthony Ngure on 6/13/2019
 *
 * @author Anthony Ngure
 */
class SmsRetrieverUtil(private val callback: Callback) {


    fun startSmsListener(fragment: Fragment) {

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        fragment.requireActivity().registerReceiver(SMSBroadcastReceiver(), intentFilter)

        val client = SmsRetriever.getClient(fragment.requireActivity())

        val task = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            BeeLog.i(TAG, "onSmsRetrieverStartSuccess")
            // Successfully started retriever, expect broadcast intent
            // ...
            callback.onSmsRetrieverStartSuccess()
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
            // ...
            BeeLog.e(it)
            BeeLog.e(TAG, "onSmsRetrieverStartFailure")
            callback.onSmsRetrieverStartFailure(it)
        }
    }

    interface Callback {
        fun onSmsRetrieverStartSuccess()
        fun onSmsRetrieverStartFailure(exception: Exception)
        fun onSmsRetrieverTimeout()
        fun onSmsRetrieverSuccess(sms: String)
    }

    private inner class SMSBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action && intent.extras != null) {
                val extras = intent.extras
                val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {

                        // Get SMS message contents
                        val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server.
                        BeeLog.i(TAG, "onSmsRetrieverSuccess, message -> $message")
                        callback.onSmsRetrieverSuccess(message)
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        BeeLog.i(TAG, "onSmsRetrieverTimeout")
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        callback.onSmsRetrieverTimeout()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "SmsRetrieverUtil"
    }


}

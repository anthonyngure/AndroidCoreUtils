package ke.co.toshngure.basecode.smsretriever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.util.AppSignatureHelper

/**
 * Created by Anthony Ngure on 6/13/2019
 *
 * @author Anthony Ngure
 */
object SmsRetrieverUtil : LifecycleObserver {

    private lateinit var mBroadcastReceiver: BroadcastReceiver
    private lateinit var fragment: Fragment
    private lateinit var callback: Callback
    private const val TAG = "SmsRetrieverUtil"
    private const val SMS_CONSENT_REQUEST = 200

    fun init(fragment: Fragment, callback: Callback) {
        this.fragment = fragment
        this.callback = callback
        fragment.viewLifecycleOwner.lifecycle.addObserver(this)
        AppSignatureHelper.getAppSignatures(fragment.requireContext())
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        BeeLog.i(TAG, "onStop")
        fragment.requireActivity().unregisterReceiver(mBroadcastReceiver)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        BeeLog.i(TAG, "onStart")
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        mBroadcastReceiver = SMSBroadcastReceiver(callback)

        fragment.requireActivity().registerReceiver(mBroadcastReceiver, intentFilter)

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
        fun onSmsRetrieverStartSuccess() {}
        fun onSmsRetrieverStartFailure(exception: Exception) {}
        fun onSmsRetrieverTimeout() {}
        fun onSmsRetrieverSuccess(sms: String?)
    }

    private class SMSBroadcastReceiver(private val callback: Callback) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action && intent.extras != null) {

                val extras = intent.extras
                val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

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




}

package ke.co.toshngure.basecode.util

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import ke.co.toshngure.basecode.logging.BeeLog

/**
 * Created by Anthony Ngure on 6/13/2019
 * @author Anthony Ngure
 */
object PhoneRetrieverUtils {

    private const val TAG = "PhoneUtils"
    private const val RESOLVE_HINT = 100

    fun init(fragment: Fragment) {

        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val apiClient = GoogleApiClient.Builder(fragment.requireContext())
            // .addConnectionCallbacks(this)
            // .enableAutoManage(fragment.requireActivity(), this)
            .addApi(Auth.CREDENTIALS_API)
            .build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest)

        try {
            fragment.startIntentSenderForResult(intent.intentSender, RESOLVE_HINT, null, 0,
                0, 0, null)
        } catch (e: IntentSender.SendIntentException) {
            BeeLog.e(e)
        }
    }

    fun onActivityResult(editText: EditText, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESOLVE_HINT && data != null && resultCode == Activity.RESULT_OK) {
            /*You will receive user selected phone number here if selected and send it to the server for request the otp*/
            val credential: Credential = data.getParcelableExtra(Credential.EXTRA_KEY)
            BeeLog.i(TAG, credential)
            editText.setText(credential.id)
        }
    }
}
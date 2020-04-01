package ke.co.toshngure.androidcoreutils.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.FrameLayout
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.BaseAppFragment
import ke.co.toshngure.basecode.smsretriever.SmsRetrieverUtil
import ke.co.toshngure.basecode.util.AppSignatureHelper
import ke.co.toshngure.basecode.smsretriever.PhoneRetrieverUtils
import kotlinx.android.synthetic.main.fragment_sms_retriever.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SmsRetrieverFragment : BaseAppFragment<Any>(), SmsRetrieverUtil.Callback {


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        layoutInflater.inflate(R.layout.fragment_sms_retriever, container, true)
    }

    override fun onStart() {
        super.onStart()
        PhoneRetrieverUtils.init(this)
        SmsRetrieverUtil.init(this, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PhoneRetrieverUtils.onActivityResult(phoneET, requestCode, resultCode, data)
    }

    @SuppressLint("SetTextI18n")
    override fun onSmsRetrieverSuccess(sms: String?) {
        sms?.let {

            val pattern = Pattern.compile("(\\d{4})")
            val matcher = pattern.matcher(sms)

            if (matcher.find()){
                phoneET.setText(matcher.group(0) ?: "no code")
            } else {
                phoneET.setText("no find")
            }
        } ?: kotlin.run {

            phoneET.setText("sms is null")
        }
    }

}

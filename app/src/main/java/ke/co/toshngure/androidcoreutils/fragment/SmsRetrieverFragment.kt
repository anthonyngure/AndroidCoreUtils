package ke.co.toshngure.androidcoreutils.fragment

import android.content.Intent
import android.widget.FrameLayout
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.BaseAppFragment
import ke.co.toshngure.basecode.smsretriever.SmsRetrieverUtil
import ke.co.toshngure.basecode.util.AppSignatureHelper
import ke.co.toshngure.basecode.smsretriever.PhoneRetrieverUtils
import kotlinx.android.synthetic.main.fragment_sms_retriever.*

class SmsRetrieverFragment : BaseAppFragment<Any>(), SmsRetrieverUtil.Callback {


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        layoutInflater.inflate(R.layout.fragment_sms_retriever, container, true)
    }

    override fun onStart() {
        super.onStart()
        PhoneRetrieverUtils.init(this)
        SmsRetrieverUtil.init(this, this)
        AppSignatureHelper.getAppSignatures(requireContext())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PhoneRetrieverUtils.onActivityResult(phoneET, requestCode, resultCode, data)
    }

    override fun onSmsRetrieverSuccess(sms: String) {
    }

}

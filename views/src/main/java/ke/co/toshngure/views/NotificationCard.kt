package ke.co.toshngure.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import ke.co.toshngure.extensions.hide
import ke.co.toshngure.extensions.showIf
import ke.co.toshngure.basecode.util.PrefUtils
import kotlinx.android.synthetic.main.view_notification_card.view.*


class NotificationCard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.view_notification_card, this, true)
    }

    fun setMessage(@StringRes heading: Int, @StringRes message: Int, prefUtils: PrefUtils) {
        val dismissed = prefUtils.getBoolean(message, false)
        this.showIf(!dismissed)
        if (!dismissed){
            messageTV.setText(message)
            headingTV.setText(heading)
            dismissBtn.setOnClickListener {
                prefUtils.writeBoolean(message, true)
                this.hide()
            }
        }
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        //super.setOnClickListener(l);
        containerLL.setOnClickListener(l)
    }
}

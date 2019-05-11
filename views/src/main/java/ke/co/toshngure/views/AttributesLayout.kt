package ke.co.toshngure.views

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.StringRes
import ke.co.toshngure.basecode.util.Spanny
import kotlinx.android.synthetic.main.view_attributes_layout.view.*


class AttributesLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var mAttributeViewParams: LinearLayout.LayoutParams

    init {
        LayoutInflater.from(context).inflate(R.layout.view_attributes_layout, this, true)
        mAttributeViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    fun setTitle(@StringRes title: Int, bold: Boolean = false): AttributesLayout {
        return setTitle(context.getString(title), bold)
    }

    fun setTitle(title: String, bold: Boolean = false): AttributesLayout {
        if (bold) {
            titleTV.text = Spanny(title, StyleSpan(Typeface.BOLD))
        } else {
            titleTV.text = title
        }
        return this
    }

    fun indent(): AttributesLayout {
        return this
    }

    fun clear(): AttributesLayout {
        containerLL.removeAllViews()
        return this
    }

    fun addAttribute(attributeView: AttributeView): AttributesLayout {
        containerLL.addView(attributeView, mAttributeViewParams)
        return this
    }
}

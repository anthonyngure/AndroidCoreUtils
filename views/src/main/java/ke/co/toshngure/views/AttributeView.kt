package ke.co.toshngure.views

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import ke.co.toshngure.extensions.hide
import ke.co.toshngure.basecode.util.Spanny
import kotlinx.android.synthetic.main.view_attribute.view.*

class AttributeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.view_attribute, this, true)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AttributeView)
        nameTV.text = typedArray.getString(R.styleable.AttributeView_avName)
        valueTV.text = typedArray.getString(R.styleable.AttributeView_avValue)
        lineView.visibility = if (typedArray.getBoolean(R.styleable.AttributeView_avShowLineView, true)) View.VISIBLE else View.GONE
        typedArray.recycle()
    }

    fun setWeights(nameWeight: Int, valueWeight: Int): AttributeView {

        nameValueLL.weightSum = (nameWeight + valueWeight).toFloat()

        val nameLayoutParams = nameTV.layoutParams as LinearLayout.LayoutParams
        nameLayoutParams.weight = nameWeight.toFloat()

        val valueLayoutParams = valueTV.layoutParams as LinearLayout.LayoutParams
        valueLayoutParams.weight = valueWeight.toFloat()
        return this

    }

    fun hideLineView(): AttributeView {
        lineView.hide()
        return this
    }

    fun setName(text: String?, bold: Boolean = false): AttributeView {
        this.nameTV.text = if (bold && !text.isNullOrEmpty()) Spanny(text, StyleSpan(Typeface.BOLD)) else text
        return this
    }

    fun setName(text: Spanny): AttributeView {
        this.nameTV.text = text
        return this
    }

    fun setValue(text: String?, bold: Boolean = false): AttributeView {
        this.valueTV.text = if (bold && !text.isNullOrEmpty()) Spanny(text, StyleSpan(Typeface.BOLD)) else text
        return this
    }

    fun setValue(text: Spanny): AttributeView {
        this.valueTV.text = text
        return this
    }

}
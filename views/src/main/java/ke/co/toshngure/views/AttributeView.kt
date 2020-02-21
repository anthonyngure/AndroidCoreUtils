package ke.co.toshngure.views

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import ke.co.toshngure.extensions.hide
import ke.co.toshngure.basecode.util.Spanny
import ke.co.toshngure.extensions.show
import ke.co.toshngure.extensions.showIf
import kotlinx.android.synthetic.main.view_attribute.view.*

class AttributeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.view_attribute, this, true)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AttributeView)
        nameTV.text = typedArray.getString(R.styleable.AttributeView_avName)
        valueTV.text = typedArray.getString(R.styleable.AttributeView_avValue)
        lineView.showIf(typedArray.getBoolean(R.styleable.AttributeView_avShowLineView, true))

        val leftDrawable = typedArray.getDrawable(R.styleable.AttributeView_avLeftDrawable)
        leftImageView.setImageDrawable(leftDrawable)
        leftImageView.showIf(leftDrawable != null)

        val rightDrawable = typedArray.getDrawable(R.styleable.AttributeView_avRightDrawable)
        rightImageView.setImageDrawable(rightDrawable)
        rightImageView.showIf(rightDrawable != null)

        typedArray.recycle()
    }

    @Suppress("unused")
    fun setWeights(nameWeight: Int, valueWeight: Int): AttributeView {

        nameValueLL.weightSum = (nameWeight + valueWeight).toFloat()

        val nameLayoutParams = nameTV.layoutParams as LinearLayout.LayoutParams
        nameLayoutParams.weight = nameWeight.toFloat()

        val valueLayoutParams = valueTV.layoutParams as LinearLayout.LayoutParams
        valueLayoutParams.weight = valueWeight.toFloat()
        return this

    }

    @Suppress("unused")
    fun hideLineView(): AttributeView {
        lineView.hide()
        return this
    }

    @Suppress("unused")
    fun setName(text: String?, bold: Boolean = false): AttributeView {
        this.nameTV.text =
            if (bold && !text.isNullOrEmpty()) Spanny(text, StyleSpan(Typeface.BOLD)) else text
        return this
    }

    @Suppress("unused")
    fun setName(text: Spanny): AttributeView {
        this.nameTV.text = text
        return this
    }

    @Suppress("unused")
    fun setValue(text: String?, bold: Boolean = false): AttributeView {
        this.valueTV.text =
            if (bold && !text.isNullOrEmpty()) Spanny(text, StyleSpan(Typeface.BOLD)) else text
        return this
    }

    @Suppress("unused")
    fun setValue(text: Spanny): AttributeView {
        this.valueTV.text = text
        return this
    }

    @Suppress("unused")
    fun setLeftDrawable(@DrawableRes drawableRes: Int) {
        this.leftImageView.show()
        this.leftImageView.setImageResource(drawableRes)
    }

    @Suppress("unused")
    fun setRightDrawable(@DrawableRes drawableRes: Int) {
        this.rightImageView.show()
        this.rightImageView.setImageResource(drawableRes)
    }

    @Suppress("unused")
    fun getValueTextView(): TextView {
        return this.valueTV
    }

    @Suppress("unused")
    fun getNameTextView(): TextView {
        return this.nameTV
    }

    @Suppress("unused")
    fun getLeftImageView(): ImageView {
        return this.leftImageView
    }

    @Suppress("unused")
    fun getRightImageView(): ImageView {
        return this.rightImageView
    }
}
package ke.co.toshngure.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import ke.co.toshngure.basecode.extensions.hide
import ke.co.toshngure.basecode.util.Spanny

class AttributeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private val valueTV: TextView
    private val nameTV: TextView
    private val lineView: LineView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_attribute, this, true)
        valueTV = findViewById(R.id.valueTV)
        nameTV = findViewById(R.id.nameTV)
        lineView = findViewById(R.id.lineView)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AttributeView)
        nameTV.text = typedArray.getString(R.styleable.AttributeView_avName)
        valueTV.text = typedArray.getString(R.styleable.AttributeView_avValue)
        lineView.visibility = if (typedArray.getBoolean(R.styleable.AttributeView_avShowLineView, true)) View.VISIBLE else View.GONE
        typedArray.recycle()
    }

    fun hideLineView(){
        lineView.hide()
    }

    fun setName(text: String?) {
        this.nameTV.text = text
    }

    fun setName(text: Spanny) {
        this.nameTV.text = text
    }

    fun setValue(text: String?) {
        this.valueTV.text = text
    }

    fun setValue(text: Spanny) {
        this.valueTV.text = text
    }

}
package ke.co.toshngure.images.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.extensions.executeAsync
import ke.co.toshngure.extensions.showIf
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.util.BaseUtils
import ke.co.toshngure.basecode.util.PrefUtilsImpl
import ke.co.toshngure.basecode.util.Spanny
import ke.co.toshngure.images.R
import ke.co.toshngure.images.data.Image
import ke.co.toshngure.images.data.ImagesDatabase
import kotlinx.android.synthetic.main.view_picked_image.view.*


class PickedImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var mImage: Image

    init {
        LayoutInflater.from(context).inflate(R.layout.view_picked_image, this, true)
        //debugTV.showIf(BeeLog.DEBUG)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec)
    }

    fun setImage(image: Image, fragment: Fragment, prefUtils: PrefUtilsImpl, cropHandler: (image: Image) -> Unit) {

        mImage = image

        debugTV.text = Spanny("${mImage.sizeInBytes / 1024} KB")
        debugTV.showIf(BeeLog.DEBUG)

        imageNI.loadImageFromMediaStore(image.resolvePath(), GlideApp.with(this))

        removeBtn.setOnClickListener { unSelectImage() }
        BaseUtils.tintImageView(removeBtn, Color.WHITE)

        setOnClickListener { cropHandler.invoke(image) }

        if (!prefUtils.getBoolean(R.string.pref_learnt_cropping_and_removing_picked_images, false)) {
            showHowToCropAndRemoveImage(fragment, prefUtils)
        }
    }



    private fun unSelectImage() {
        context?.let {
            executeAsync { ImagesDatabase.getInstance(it).images().update(mImage.copy(selected = false)) }
        }
    }


    private fun showHowToCropAndRemoveImage(fragment: Fragment, prefUtils: PrefUtilsImpl) {
        TapTargetSequence(fragment.activity)
            .target(
                TapTarget.forView(this, "Edit and Crop Image", "Tap to crop and edit selected image")
                    //.id(R.id.guide_booking_appointment)
                    .targetRadius(100)
                    .outerCircleAlpha(0.8f)
                    .outerCircleColorInt(BaseUtils.getColor(context, R.attr.colorPrimary))
                    .transparentTarget(true)
                    .cancelable(true)
            )
            .target(
                TapTarget.forView(removeBtn, "Remove Image", "Tap to remove selected image")
                    .outerCircleAlpha(0.8f)
                    .outerCircleColorInt(BaseUtils.getColor(context, R.attr.colorPrimary))
                    .transparentTarget(true)
                    .cancelable(true)
            )
            .listener(object : TapTargetSequence.Listener {

                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    prefUtils.writeBoolean(R.string.pref_learnt_cropping_and_removing_picked_images, true)
                }

                override fun onSequenceFinish() {
                    prefUtils.writeBoolean(R.string.pref_learnt_cropping_and_removing_picked_images, true)
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

                }

            }).start()
    }


    companion object {
        private const val TAG = "PickedImageView"
    }

}
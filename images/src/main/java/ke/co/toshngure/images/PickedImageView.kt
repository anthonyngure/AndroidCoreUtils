package ke.co.toshngure.images

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yalantis.ucrop.UCrop
import ke.co.toshngure.images.data.Image
import ke.co.toshngure.pennycharm.core.GlideRequests
import kotlinx.android.synthetic.main.view_picked_image.view.*
import java.io.File


class PickedImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)

    : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_picked_image, this, true)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec)
    }

    /**
     * @param position is used as request code during crop and assigned to image displayPosition
     * The image displayPosition is used to determin which image to update after cropping
     * If cropped image uri is not empty we init the cropped uri else we init the MediaStore uri
     */
    fun setImage(
        image: Image, glideRequests: GlideRequests, fragment: Fragment,
        position: Int, removeCall: (image: Image) -> Unit
    ) {

        image.displayPosition = position

        val path = image.croppedPath?.let { it } ?: image.path

        imageNI.loadImageFromMediaStore(path, glideRequests)

        removeBtn.setOnClickListener {
            removeCall(image)
        }

        setOnClickListener {
            val destinationUri =
                Uri.fromFile(File(context.cacheDir, "cropped_" + SystemClock.elapsedRealtime() + ".jpg"))
            context?.let { ctx ->
                val options = UCrop.Options()
                options.setToolbarWidgetColor(ContextCompat.getColor(ctx, R.color.colorAccent))
                options.setStatusBarColor(ContextCompat.getColor(ctx, R.color.colorAccent))
                options.setToolbarColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                options.setToolbarCropDrawable(R.drawable.ic_done_black_24dp)
                options.setToolbarCancelDrawable(R.drawable.ic_clear_black_24dp)
                options.setShowCropFrame(true)
                options.setShowCropGrid(true)
                UCrop.of(Uri.fromFile(File(path)), destinationUri)
                    .withOptions(options)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(612, 612)
                    .start(ctx, fragment, image.displayPosition)
            }
        }
    }


}
package ke.co.toshngure.images.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.widget.Toolbar
import com.otaliastudios.cameraview.AspectRatio
import com.otaliastudios.cameraview.PictureResult
import ke.co.toshngure.basecode.app.BaseAppActivity
import ke.co.toshngure.basecode.extensions.executeAsync
import ke.co.toshngure.images.R
import ke.co.toshngure.images.data.Image
import kotlinx.android.synthetic.main.activity_picture_preview.*
import java.io.File
import java.lang.ref.WeakReference


@Suppress("DEPRECATION")
class PicturePreviewActivity : BaseAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_preview)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val result = if (pictureResult == null) null else pictureResult!!.get()
        if (result == null) {
            finish()
            return
        }
        //val delay = intent.getLongExtra("delay", 0)
        //val ratio = AspectRatio.of(result.size)
        //captureLatency.setTitleAndMessage("Approx. latency", delay + " milliseconds");
        //captureResolution.setTitleAndMessage("Resolution", result.getSize() + " (" + ratio + ")");
        //exifRotation.setTitleAndMessage("EXIF rotation", result.getRotation() + "");
        result.toBitmap(1000, 1000) {
            imageView.setImageBitmap(it)
        }


        doneBtn.setOnClickListener {

            executeAsync({
                //val file = File(cacheDir, "captured_" + System.currentTimeMillis() + ".jpg")
                val path = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
                val directoryCreated = if (!path.exists()) {
                    path.mkdir()
                } else {
                    true
                }

                if (directoryCreated) {
                    val file = File(path, "captured_" + System.currentTimeMillis() + ".jpg")
                    file.writeBytes(result.data)
                    val image = Image(
                        fromCamera = true,
                        selected = true,
                        selectedAt = System.currentTimeMillis(),
                        dateAdded = System.currentTimeMillis(),
                        path = file.path,
                        sizeInBytes = file.length()
                    )
                    image
                } else {
                    null
                }
            }) {
                it?.let {
                    val data = Intent().apply {
                        putExtra(CameraActivity.EXTRA_IMAGE, it)
                    }
                    setResult(Activity.RESULT_OK, data)
                } ?: run {
                    setResult(Activity.RESULT_CANCELED)
                }
                this.finish()
            }


            /*executeAsync {
                val file = File(cacheDir, "captured_" + System.currentTimeMillis() + ".jpg")
                file.writeBytes(result.data)
                val image = Image(
                    fromCamera = true,
                    selected = true,
                    selectedAt = System.currentTimeMillis(),
                    dateAdded = System.currentTimeMillis(),
                    path = file.path,
                    sizeInBytes = file.length()
                )
                BeeLog.i(TAG, image)
                ImagesDatabase.getInstance(this@PicturePreviewActivity).images().insert(image)
            }
            setResult(Activity.RESULT_OK)
            this.finish()*/
        }

        cancelBtn.setOnClickListener { this.finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            setPictureResult(null)
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    companion object {

        private const val TAG = "PicturePreviewActivity"

        private var pictureResult: WeakReference<PictureResult>? = null

        fun setPictureResult(im: PictureResult?) {
            pictureResult = if (im != null) WeakReference(im) else null
        }
    }
}

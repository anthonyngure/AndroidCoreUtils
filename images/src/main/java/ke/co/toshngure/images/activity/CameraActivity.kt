package ke.co.toshngure.images.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.otaliastudios.cameraview.*
import ke.co.toshngure.basecode.app.BaseAppActivity
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.images.R
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : BaseAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cameraView.setLifecycleOwner(this)

        cameraView.mode = Mode.PICTURE
        cameraView.audio = Audio.OFF

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)

                if (cameraView.isTakingVideo) {
                    message("Captured while taking video. Size=" + result.size, false)
                    return
                }

                PicturePreviewActivity.setPictureResult(result)
                val intent = Intent(this@CameraActivity, PicturePreviewActivity::class.java)
                startActivityForResult(intent, PREVIEW_REQUEST_CODE)
            }

        })

        captureIV.setOnClickListener { capturePicture() }

        toggleCameraBtn.setOnClickListener {
            toggleCamera()
        }

        flashBtn.setOnClickListener { toggleFlash() }
    }

    private fun message(content: String, important: Boolean) {
        try {
            val length = if (important) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            Toast.makeText(this, content, length).show()
        } catch (e: Exception) {
            BeeLog.e(e)
        }
    }

    private fun toggleCamera() {
        if (cameraView.isTakingPicture || cameraView.isTakingVideo) return

        toggleCameraBtn.setIconResource(
            if (cameraView.toggleFacing() == Facing.BACK)
                R.drawable.ic_camera_rear_black_24dp
            else R.drawable.ic_camera_front_black_24dp
        )

    }

    private fun toggleFlash() {
        /*if (cameraView.isTakingPicture || cameraView.isTakingVideo) return

        when(flashBtn.iconR)

        flashBtn.setIconResource(if (cameraView.flash() == Facing.BACK)
            R.drawable.ic_camera_rear_black_24dp
        else R.drawable.ic_camera_front_black_24dp)*/

    }

    private fun capturePicture() {
        if (cameraView.mode == Mode.VIDEO) {
            message("Can't take HQ pictures while in VIDEO mode.", false)
            return
        }
        if (cameraView.isTakingPicture) return
        // mCaptureTime = System.currentTimeMillis()
        message("Capturing picture...", false)
        cameraView.takePicture()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PREVIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK, data)
            this@CameraActivity.finish()
        } else {
            message("Unable to capture an image", true)
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }


    companion object {
        private const val PREVIEW_REQUEST_CODE = 108
        const val EXTRA_IMAGE: String = "extra_captured_image"
    }


}

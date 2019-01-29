package ke.co.toshngure.images.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.otaliastudios.cameraview.*
import ke.co.toshngure.basecode.app.BaseAppActivity
import kotlinx.android.synthetic.main.activity_camera.*
import ke.co.toshngure.images.R
import kotlinx.android.synthetic.main.fragment_images_picker.*

class CameraActivity : BaseAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cameraView.setLifecycleOwner(this)

        cameraView.mode = Mode.PICTURE
        cameraView.audio = Audio.OFF

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
            }

        })

        captureIV.setOnClickListener {capturePicture()}

        toggleCameraBtn.setOnClickListener {
            toggleCamera()
        }

        flashBtn.setOnClickListener { toggleFlash() }
    }

    private fun toggleCamera() {
        if (cameraView.isTakingPicture || cameraView.isTakingVideo) return

        toggleCameraBtn.setIconResource(if (cameraView.toggleFacing() == Facing.BACK)
            R.drawable.ic_camera_rear_black_24dp
        else R.drawable.ic_camera_front_black_24dp)

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
            // message("Can't take HQ pictures while in VIDEO mode.", false)
            return
        }
        if (cameraView.isTakingPicture) return
        // mCaptureTime = System.currentTimeMillis()
        //message("Capturing picture...", false)
        cameraView.takePicture()
    }


}

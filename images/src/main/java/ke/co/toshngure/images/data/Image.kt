package ke.co.toshngure.images.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

data class Image(
    @PrimaryKey var id: Long = 0,
    var name: String? = null,
    var path: String? = null,
    var bucket: String? = null,
    @Ignore var croppedUri: Uri? = null,
    var croppedPath: String? = null,
    var fromCamera: Boolean = false,
    var fromNetwork: Boolean = false,
    var displayPosition: Int = 0,
    var selected: Boolean = false
)


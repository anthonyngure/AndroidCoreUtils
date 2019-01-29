package ke.co.toshngure.images.model

import android.net.Uri

data class Image( val id: Long = 0,
                 private val name: String? = null,
                 var path: String,
                 var bucket: String? = null,
                 var uri: Uri? = null,
                 var croppedUri: Uri? = null,
                 var croppedPath: String? = null,
                 var fromCamera: Boolean = false,
                 var fromNetwork: Boolean = false,
                 var displayPosition: Int = 0,
                 var selected: Boolean = false)


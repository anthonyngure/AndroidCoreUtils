package ke.co.toshngure.images.data

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "images")
@Parcelize
data class Image(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String? = null,
    var path: String? = null,
    var compressedPath: String? = null,
    var croppedPath: String? = null,
    var bucket: String? = null,
    var dateAdded: Long = 0,
    var fromCamera: Boolean = false,
    var fromNetwork: Boolean = false,
    var displayPosition: Int = 0,
    var sizeInBytes: Long = 0,
    var selected: Boolean = false,
    var selectedAt: Long = 0
) : Parcelable {

    fun resolvePath(): String? {
        // Cropping is the last manipulation
        return if (!this.croppedPath.isNullOrEmpty()) {
            this.croppedPath
        }
        // Compressing is the first manipulation
        else if (!this.compressedPath.isNullOrEmpty()) {
            this.compressedPath
        } else {
            this.path
        }
    }

    companion object {
        val DIFF_UTIL_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Image>() {

            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.selected == newItem.selected
                        && oldItem.compressedPath == newItem.compressedPath
                        && oldItem.croppedPath == newItem.croppedPath
            }

            override fun getChangePayload(oldItem: Image, newItem: Image): Any? {
                // return super.getChangePayload(oldItem, newItem)
                return newItem
            }
        }
    }

}

package ke.co.toshngure.androidcoreutils.photos

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "photos")
data class Photo(
        @field:SerializedName("id") @ColumnInfo(name = "id") @PrimaryKey val id: Long,
        @field:SerializedName("albumId") @ColumnInfo(name = "album_id") val albumId: Long,
        @field:SerializedName("title") @ColumnInfo(name = "title") val title: String,
        @field:SerializedName("url") @ColumnInfo(name = "url") val url: String,
        @field:SerializedName("thumbnailUrl") @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String) {


    companion object {
        val DIFF_UTIL_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem == newItem

        }
    }
}

package ke.co.toshngure.androidcoreutils.albums

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "albums")
data class Album(
        @field:SerializedName("id") @PrimaryKey @ColumnInfo(name = "id") val id: Long,
        @field:SerializedName("userId") @ColumnInfo(name = "user_id") val userId: Long,
        @field:SerializedName("title") @ColumnInfo(name = "title") val title: String,
        @field:SerializedName("views") @ColumnInfo(name = "views") val views: Int
) {
    companion object {
        private val PAYLOAD_SCORE = Any()
        val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<Album>() {

            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean =
                    oldItem.id == newItem.id

            override fun getChangePayload(oldItem: Album, newItem: Album): Any? {
                return if (sameExceptScore(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptScore(oldItem: Album, newItem: Album): Boolean {
            // DON'T do this copy in a real app, it is just convenient here for the demo :)
            // because reddit randomizes scores, we want to pass it as a payload to minimize
            // UI updates between refreshes
            return oldItem.copy(views = newItem.views) == newItem
        }
    }
}
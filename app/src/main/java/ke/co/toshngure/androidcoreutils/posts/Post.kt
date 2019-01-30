package ke.co.toshngure.androidcoreutils.posts

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
        @ColumnInfo(name = "id") @PrimaryKey val id: Long,
        @ColumnInfo(name = "user_id") val userId: Long,
        @ColumnInfo(name = "title") val title: String?,
        @ColumnInfo(name = "body") val body: String?,
        @ColumnInfo(name = "views") val views: Int
) {


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem

        }
    }
}
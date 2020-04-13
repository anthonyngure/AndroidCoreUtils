package ke.co.toshngure.androidcoreutils.posts

import android.annotation.SuppressLint
import android.view.View
import ke.co.toshngure.basecode.app.GlideRequests
import ke.co.toshngure.basecode.paging.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.util.Spanny
import kotlinx.android.synthetic.main.item_post.view.*

class PostViewHolder(view: View, private val glide: GlideRequests) : BaseItemViewHolder<Post>(view) {


    @SuppressLint("SetTextI18n")
    override fun bindTo(item: Post) {
        super.bindTo(item)
        itemView.titleTV.text = Spanny(item.id.toString()).append(" - ").append(item.title ?: "Loading...")
        itemView.bodyTV.text = item.body ?: "..."
        itemView.viewsTV.text = "${item.views} views"
    }
}
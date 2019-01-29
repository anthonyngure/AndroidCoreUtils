package ke.co.toshngure.androidcoreutils.posts

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.util.Spanny
import ke.co.toshngure.pennycharm.core.GlideRequests
import kotlinx.android.synthetic.main.item_post.view.*

class PostViewHolder(view: View, private val glide: GlideRequests) : BaseItemViewHolder<Post>(view) {

    init {
        view.setOnClickListener {
            item?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindTo(item: Post) {
        super.bindTo(item)
        itemView.titleTV.text = Spanny(item.id.toString()).append(" - ").append(item.title ?: "Loading...")
        itemView.bodyTV.text = item.body ?: "..."
        itemView.viewsTV.text = "${item.views} views"
        itemView.thumbnailIV.loadImageFromNetwork(item.thumbnail, glide)
    }
}
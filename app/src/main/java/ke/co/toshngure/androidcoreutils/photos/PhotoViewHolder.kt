package ke.co.toshngure.androidcoreutils.photos

import android.view.View
import ke.co.toshngure.basecode.app.GlideRequests
import ke.co.toshngure.basecode.paging.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.util.Spanny
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoViewHolder(itemView: View, private val glide: GlideRequests) : BaseItemViewHolder<Photo>(itemView) {


    override fun bindTo(item: Photo) {
        super.bindTo(item)
        itemView.titleTV.text = Spanny(item.id.toString()).append(" - ").append(item.title)
        itemView.photoIV.loadImageFromNetwork(item.url, glide)
    }

}
package ke.co.toshngure.androidcoreutils.albums

import android.view.View
import ke.co.toshngure.basecode.paging.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.util.Spanny
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumViewHolder(itemView: View) : BaseItemViewHolder<Album>(itemView) {

    override fun bindTo(item: Album) {
        super.bindTo(item)
        itemView.titleTV.text = Spanny(item.id.toString())
                .append(" - ")
                .append(item.title)
                .append("\n").append(item.toString())

    }

}
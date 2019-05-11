package ke.co.toshngure.images.view

import android.view.View
import ke.co.toshngure.basecode.app.GlideRequests
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.images.data.Image
import kotlinx.android.synthetic.main.item_image_selection.view.*

class ImageSelectionViewHolder(itemView: View, private val mGlideRequests: GlideRequests) : BaseItemViewHolder<Image>(itemView) {


    override fun bindTo(item: Image) {
        super.bindTo(item)
        itemView.imageNI.loadImageFromMediaStore(item.path, mGlideRequests)
        itemView.backgroundView.visibility = if (item.selected) View.VISIBLE else View.GONE
        itemView.selectionIV.visibility = if (item.selected) View.VISIBLE else View.GONE
    }

    override fun update(item: Image) {
        super.update(item)
        itemView.backgroundView.visibility = if (item.selected) View.VISIBLE else View.GONE
        itemView.selectionIV.visibility = if (item.selected) View.VISIBLE else View.GONE
    }
}
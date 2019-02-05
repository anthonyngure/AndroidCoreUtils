package ke.co.toshngure.androidcoreutils.users

import android.view.View
import ke.co.toshngure.basecode.app.GlideRequests
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.util.Spanny
import kotlinx.android.synthetic.main.item_user.view.*

class UserViewHolder(view: View, private val glide: GlideRequests) : BaseItemViewHolder<User>(view) {


    override fun bindTo(item: User) {
        super.bindTo(item)
        itemView.nameTV.text = Spanny(item.id.toString()).append(" - ").append(item.name)
        //itemView.avatarNI.loadImageFromNetwork(item.avatarUrl, glide)
    }
}
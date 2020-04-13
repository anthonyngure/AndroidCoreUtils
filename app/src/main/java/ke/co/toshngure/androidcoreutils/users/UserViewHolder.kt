package ke.co.toshngure.androidcoreutils.users

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.View
import ke.co.toshngure.basecode.paging.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.util.Spanny
import kotlinx.android.synthetic.main.item_user.view.*

class UserViewHolder(view: View) : BaseItemViewHolder<User>(view) {

    override fun bindTo(item: User) {
        super.bindTo(item)
        itemView.nameTV.text =
            Spanny(item.id.toString()).append(" - ")
                .append(item.name, StyleSpan(Typeface.BOLD))
                .append(item.username, Typeface.NORMAL)
        itemView.phoneTV.text = item.phone
        itemView.emailTV.text = item.email
    }
}
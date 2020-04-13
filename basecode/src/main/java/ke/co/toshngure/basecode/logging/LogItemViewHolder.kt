package ke.co.toshngure.basecode.logging

import android.view.View
import ke.co.toshngure.basecode.paging.adapter.BaseItemViewHolder
import kotlinx.android.synthetic.main.basecode_item_log_item.view.*

class LogItemViewHolder(itemView: View) : BaseItemViewHolder<LogItem>(itemView) {

    override fun bindTo(item: LogItem) {
        super.bindTo(item)
        itemView.titleTV.text = item.title
        itemView.detailsTV.text = item.details
    }
}
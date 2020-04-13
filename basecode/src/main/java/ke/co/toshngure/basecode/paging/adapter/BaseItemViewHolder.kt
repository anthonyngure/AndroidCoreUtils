
package ke.co.toshngure.basecode.paging.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseItemViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    protected var item: T? = null

    open fun bindTo(item: T) {
        this.item = item
    }

    open fun update(item: T) {
        this.item = item
    }


}
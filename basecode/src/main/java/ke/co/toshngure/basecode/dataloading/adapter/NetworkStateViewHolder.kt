package ke.co.toshngure.basecode.dataloading.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.dataloading.NetworkState
import ke.co.toshngure.basecode.dataloading.Status
import ke.co.toshngure.basecode.logging.BeeLog

/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateViewHolder(view: View, private val retryCallback: () -> Unit)
    : RecyclerView.ViewHolder(view) {

    private val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
    private val retryBtn = itemView.findViewById<Button>(R.id.retryBtn)
    private val messageTV = itemView.findViewById<TextView>(R.id.messageTV)

    init {
        retryBtn.setOnClickListener {
            retryCallback()
        }
    }

    fun bindTo(item: NetworkState?) {
        BeeLog.i(TAG, item)
        progressBar.visibility = toVisibility(item?.status == Status.RUNNING)
        retryBtn.visibility = toVisibility(item?.status == Status.FAILED)
        messageTV.visibility = toVisibility(item?.message != null)
        messageTV.text = item?.message
    }

    companion object {
        private const val TAG = "NetworkStateViewHolder"
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basecode_item_network_state, parent, false)
            return NetworkStateViewHolder(view, retryCallback)
        }

        private fun toVisibility(constraint: Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}

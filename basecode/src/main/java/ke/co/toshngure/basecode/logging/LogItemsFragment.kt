package ke.co.toshngure.basecode.logging

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import ke.co.toshngure.basecode.BuildConfig
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.dataloading.PagingConfig
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import kotlinx.android.synthetic.main.basecode_fragment_log_items_top_view.*

class LogItemsFragment : PagingFragment<LogItem, LogItem, Any>() {

    override fun getPagingConfig(): PagingConfig<LogItem, LogItem> {
        return PagingConfig(
                layoutRes = R.layout.basecode_item_log_item,
                diffUtilItemCallback = LogItem.DIFF_CALLBACK,
                repository = LogItemRepository()
        )
    }

    override fun onSetUpTopView(container: FrameLayout) {
        super.onSetUpTopView(container)
        layoutInflater.inflate(R.layout.basecode_fragment_log_items_top_view, container, true)
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<LogItem> {
        return LogItemViewHolder(itemView)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subTagsMS.setOnItemSelectedListener { _, _, _, item ->
            toastDebug(item)
            if (item == ALL_SUB_TAGS) {
                loadWithArgs(null)
            } else {
                val args = Bundle()
                args.putString(EXTRA_SUB_TAG, item.toString())
                loadWithArgs(args)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*Sub Tags*/
        LogItemsDatabase.getInstance().logItems().getSubTagList().observe(this, Observer {
            subTagsMS?.let { spinner ->
                val folderNames = arrayListOf<String>()
                folderNames.addAll(it)
                folderNames.add(ALL_SUB_TAGS)
                folderNames.sortBy { folder -> folder }
                spinner.setItems(folderNames)
                spinner.selectedIndex = folderNames.indexOf(ALL_SUB_TAGS)
            }
        })
    }

    companion object {
        const val EXTRA_SUB_TAG = "${BuildConfig.LIBRARY_PACKAGE_NAME}_extra_sub_tag"
        private const val ALL_SUB_TAGS = "All sub tags"
    }
}
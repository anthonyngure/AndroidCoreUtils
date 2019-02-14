package ke.co.toshngure.basecode.dataloading

import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.data.ItemRepository

data class PagingFragmentConfig<Model, LoadedModel>(
        @LayoutRes val layoutRes: Int,
        val withDivider: Boolean = true,
        val refreshEnabled: Boolean = false,
        val showDialog: Boolean = false,
        val diffUtilItemCallback: DiffUtil.ItemCallback<LoadedModel>,
        val repository: ItemRepository<Model, LoadedModel>,
        val itemClickListener: ItemsAdapter.OnItemClickListener<LoadedModel>? = null,
        @StringRes val noDataMessage: Int = R.string.message_empty_data,
        @DrawableRes val noDataIcon: Int = R.drawable.ic_cloud_queue_black_24dp
)
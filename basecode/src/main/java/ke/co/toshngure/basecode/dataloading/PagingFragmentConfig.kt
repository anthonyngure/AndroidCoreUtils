package ke.co.toshngure.basecode.dataloading

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.data.ItemRepository

data class PagingFragmentConfig<Model, LoadedModel>(
    @LayoutRes val layoutRes: Int,
    val withDivider: Boolean = true,
    val autoLoad: Boolean = true,
    val refreshEnabled: Boolean = true,
    val showDialog: Boolean = false,
    val diffUtilItemCallback: DiffUtil.ItemCallback<LoadedModel>,
    val repository: ItemRepository<Model, LoadedModel>,
    val itemClickListener: ItemsAdapter.OnItemClickListener<LoadedModel>? = null
)
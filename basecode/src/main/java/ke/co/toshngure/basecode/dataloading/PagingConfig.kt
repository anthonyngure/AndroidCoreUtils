package ke.co.toshngure.basecode.dataloading

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.data.ItemRepository

data class PagingConfig<Model, LoadedModel>(
        @LayoutRes val layoutRes: Int,
        val withDivider: Boolean = true,
        val diffUtilItemCallback: DiffUtil.ItemCallback<LoadedModel>,
        val repository: ItemRepository<Model, LoadedModel>,
        val itemClickListener: ItemsAdapter.OnItemClickListener<LoadedModel>? = null,
        val arguments: Bundle? = null)
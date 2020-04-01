package ke.co.toshngure.basecode.dataloading

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.data.ItemRepository

data class PagingConfig<Model, LoadedModel>(
        @LayoutRes val layoutRes: Int,
        val withDivider: Boolean = true,
        val diffUtilItemCallback: DiffUtil.ItemCallback<LoadedModel>,
        val repository: ItemRepository<Model, LoadedModel>,
        val noDataLayoutClickLister: View.OnClickListener? = null,
        val errorLayoutClickLister: View.OnClickListener? = null,
        val itemClickListener: ItemsAdapter.OnItemClickListener<LoadedModel>? = null,
        val arguments: Bundle? = null,
        val itemAnimator: RecyclerView.ItemAnimator? = SlideInUpAnimator())
package ke.co.toshngure.androidcoreutils.posts

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.dataloading.Config
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.pennycharm.core.GlideApp

class PostsFragment : PagingFragment<Post, Post>() {

    override fun createRepository(): ItemRepository<Post, Post> {
        return PostRepository()
    }

    override fun createDiffUtilItemCallback(): DiffUtil.ItemCallback<Post> {
        return Post.DIFF_CALLBACK
    }

    override fun getConfig(): Config {
        return Config(layoutRes = R.layout.item_post, withDivider = false)
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Post> {
        return PostViewHolder(itemView, GlideApp.with(this))
    }
}

package ke.co.toshngure.androidcoreutils.posts

import android.view.View
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.PagingFragmentConfig
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.data.ItemRepository

class PostsFragment : PagingFragment<Post, Post>() {

    override fun getConfig(): PagingFragmentConfig<Post, Post> {
        return PagingFragmentConfig(
            layoutRes = R.layout.item_post,
            withDivider = false,
            refreshEnabled = true,
            diffUtilItemCallback = Post.DIFF_CALLBACK,
            repository = PostRepository()
        )
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Post> {
        return PostViewHolder(itemView, GlideApp.with(this))
    }

}

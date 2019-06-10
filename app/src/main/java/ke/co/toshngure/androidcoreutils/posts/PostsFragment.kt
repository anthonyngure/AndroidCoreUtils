package ke.co.toshngure.androidcoreutils.posts

import android.view.View
import android.widget.FrameLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.app.LoadingConfig
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.PagingConfig
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder

class PostsFragment : PagingFragment<Post, Post,Any>() {


    override fun getPagingConfig(): PagingConfig<Post, Post> {
        return PagingConfig(
            layoutRes = R.layout.item_post,
            withDivider = false,
            diffUtilItemCallback = Post.DIFF_CALLBACK,
            repository = PostRepository()
        )
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Post> {
        return PostViewHolder(itemView, GlideApp.with(this))
    }

    override fun onSetUpCollapsibleView(container: FrameLayout) {
        super.onSetUpCollapsibleView(container)
        layoutInflater.inflate(R.layout.fragment_posts_collapsible_view, container, true)
    }

    override fun onSetUpTopView(container: FrameLayout) {
        super.onSetUpTopView(container)
        layoutInflater.inflate(R.layout.fragment_posts_top_view, container, true)
    }

    override fun getLoadingConfig(): LoadingConfig {
        return LoadingConfig(refreshEnabled = true)
    }

    override fun onRefresh(swipeRefreshLayout: SwipeRefreshLayout?) {
        super.onRefresh(swipeRefreshLayout)
    }

}

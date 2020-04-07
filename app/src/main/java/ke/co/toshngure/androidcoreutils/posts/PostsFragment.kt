package ke.co.toshngure.androidcoreutils.posts

import android.view.View
import ke.co.toshngure.androidcoreutils.database.AppDatabase
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.app.LoadingConfig
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.PagingConfig
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.extensions.executeAsync

class PostsFragment : PagingFragment<Post, Post, Any>() {


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

    override fun getLoadingConfig(): LoadingConfig {
        return LoadingConfig(refreshEnabled = true)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /*override fun onSetUpCollapsibleViewContainer(
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        container: FrameLayout
    ) {
        super.onSetUpCollapsibleViewContainer(appBarLayout, collapsingToolbarLayout, container)

        appBarLayout.setOnDragListener { _, _ -> false }
        collapsingToolbarLayout.setOnDragListener { _, _ -> false }

        layoutInflater.inflate(R.layout.fragment_posts_collapsible_view, container, true)
        button.setOnClickListener {
            toast("Hello")
        }
    }

    override fun onSetUpBottomViewContainer(container: FrameLayout) {
        super.onSetUpBottomViewContainer(container)
        layoutInflater.inflate(R.layout.fragment_posts_bottom_view, container, true)
    }*/


}

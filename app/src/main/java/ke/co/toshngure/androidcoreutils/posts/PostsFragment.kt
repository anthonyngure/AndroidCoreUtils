package ke.co.toshngure.androidcoreutils.posts

import android.view.View
import android.widget.FrameLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ke.co.toshngure.androidcoreutils.AppDatabase
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.app.LoadingConfig
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.PagingConfig
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.extensions.executeAsync

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

    override fun getLoadingConfig(): LoadingConfig {
        return LoadingConfig(refreshEnabled = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        executeAsync {
            AppDatabase.getInstance().posts().deleteAll()
        }
    }

}

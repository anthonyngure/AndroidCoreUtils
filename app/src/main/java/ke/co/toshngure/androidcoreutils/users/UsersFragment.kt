package ke.co.toshngure.androidcoreutils.users

import android.view.View
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.PagingFragmentConfig
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter

class UsersFragment : PagingFragment<User, User, Any>(), ItemsAdapter.OnItemClickListener<User> {

    override fun onClick(item: User) {
        toast(item.name)
    }

    override fun getConfig(): PagingFragmentConfig<User, User> {
        return PagingFragmentConfig(
            layoutRes = R.layout.item_user,
            withDivider = false,
            refreshEnabled = false,
            diffUtilItemCallback = User.DIFF_CALLBACK,
            repository = UserRepository(),
            itemClickListener = this
        )
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<User> {
        return UserViewHolder(itemView, GlideApp.with(this))
    }

}

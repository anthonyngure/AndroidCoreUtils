package ke.co.toshngure.androidcoreutils.users

import android.view.View
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.paging.PagingConfig
import ke.co.toshngure.basecode.paging.PagingFragment
import ke.co.toshngure.basecode.paging.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.paging.adapter.ItemsAdapter

class UsersFragment : PagingFragment<User, User, Any>(), ItemsAdapter.OnItemClickListener<User> {

    override fun onClick(item: User) {
        toast(item.name)
    }

    override fun getPagingConfig(): PagingConfig<User, User> {
        return PagingConfig(
            layoutRes = R.layout.item_user,
            withDivider = false,
            diffUtilItemCallback = User.DIFF_CALLBACK,
            repository = UserRepository(),
            itemClickListener = this
        )
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<User> {
        return UserViewHolder(itemView)
    }

}

package ke.co.toshngure.androidcoreutils.photos

import android.view.View
import ke.co.toshngure.androidcoreutils.Extras
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.PagingFragmentConfig
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder

class PhotosFragment : PagingFragment<Photo, Photo>() {




    override fun getConfig(): PagingFragmentConfig<Photo, Photo> =
        PagingFragmentConfig(
            layoutRes = R.layout.item_photo,
            withDivider = false,
            refreshEnabled = false,
            diffUtilItemCallback = Photo.DIFF_UTIL_ITEM_CALLBACK,
            repository = PhotoRepository(arguments?.getLong(Extras.ALBUM_ID) ?: 1)
        )


    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Photo> =
        PhotoViewHolder(itemView, GlideApp.with(itemView.context))
}

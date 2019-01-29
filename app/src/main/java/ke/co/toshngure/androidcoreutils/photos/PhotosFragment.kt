package ke.co.toshngure.androidcoreutils.photos

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import ke.co.toshngure.androidcoreutils.Extras
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.androidcoreutils.photos.Photo
import ke.co.toshngure.androidcoreutils.photos.PhotoRepository
import ke.co.toshngure.androidcoreutils.photos.PhotoViewHolder
import ke.co.toshngure.basecode.dataloading.Config
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.pennycharm.core.GlideApp

class PhotosFragment : PagingFragment<Photo, Photo>() {


    override fun createRepository(): ItemRepository<Photo, Photo> =
        PhotoRepository(arguments?.getLong(Extras.ALBUM_ID) ?: 1)

    override fun createDiffUtilItemCallback(): DiffUtil.ItemCallback<Photo> =
        Photo.DIFF_UTIL_ITEM_CALLBACK

    override fun getConfig(): Config =
        Config(layoutRes = R.layout.item_photo, withDivider = false, refreshEnabled = false)


    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Photo> =
        PhotoViewHolder(itemView, GlideApp.with(itemView.context))
}

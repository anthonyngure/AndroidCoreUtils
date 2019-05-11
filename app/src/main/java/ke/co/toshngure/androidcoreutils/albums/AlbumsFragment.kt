package ke.co.toshngure.androidcoreutils.albums

import android.os.Bundle
import android.view.View
import ke.co.toshngure.androidcoreutils.Extras
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.dataloading.PagingConfig
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter

class AlbumsFragment : PagingFragment<Album, Album,Any>(), ItemsAdapter.OnItemClickListener<Album> {

    override fun onClick(item: Album) {
        val args = Bundle()
        args.putLong(Extras.ALBUM_ID, item.id)
        navigateWithPermissionsCheck(R.id.photosFragment, args)
    }

    override fun getPagingConfig(): PagingConfig<Album, Album> {
        return PagingConfig(
            layoutRes = R.layout.item_album,
            diffUtilItemCallback = Album.DIFF_UTIL_CALLBACK,
            repository = AlbumRepository(),
            itemClickListener = this
        )
    }


    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Album> = AlbumViewHolder(itemView)

}
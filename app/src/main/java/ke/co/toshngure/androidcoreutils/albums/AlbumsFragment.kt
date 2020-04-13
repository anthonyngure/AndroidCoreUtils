package ke.co.toshngure.androidcoreutils.albums

import android.os.Bundle
import android.view.View
import ke.co.toshngure.androidcoreutils.util.Extras
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.app.LoadingConfig
import ke.co.toshngure.basecode.paging.PagingConfig
import ke.co.toshngure.basecode.paging.PagingFragment
import ke.co.toshngure.basecode.paging.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.paging.adapter.ItemsAdapter

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

    override fun getLoadingConfig(): LoadingConfig {
        return super.getLoadingConfig()
            .copy(refreshEnabled = true)
    }


    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Album> = AlbumViewHolder(itemView)

}
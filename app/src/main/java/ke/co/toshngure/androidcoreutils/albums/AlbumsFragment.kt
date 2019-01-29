package ke.co.toshngure.androidcoreutils.albums

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import ke.co.toshngure.androidcoreutils.Extras
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.basecode.dataloading.Config
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.extensions.navigate

class AlbumsFragment : PagingFragment<Album, Album>(), ItemsAdapter.OnItemClickListener<Album> {

    override fun onClick(item: Album) {
        val args = Bundle()
        args.putLong(Extras.ALBUM_ID, item.id)
        view?.navigate(R.id.photosFragment, args)
    }


    override fun createRepository(): ItemRepository<Album, Album> = AlbumRepository()

    override fun createDiffUtilItemCallback(): DiffUtil.ItemCallback<Album> = Album.DIFF_UTIL_CALLBACK

    override fun getConfig(): Config {
        return Config(layoutRes = R.layout.item_album)
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Album> = AlbumViewHolder(itemView)

    override fun createOnItemClickListener(): ItemsAdapter.OnItemClickListener<Album>? {
        return this
    }

}
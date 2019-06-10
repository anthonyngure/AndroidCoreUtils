package ke.co.toshngure.images.data

import android.content.Context
import androidx.paging.DataSource
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.images.fragment.ImagesPickerFragment

class ImageRepository(private val context: Context) : ItemRepository<Image, Image>() {

    override fun getItemId(item: Image): Long {
        return item.id ?: 0
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Image, Image> {
        return ItemRepositoryConfig(syncClass = Image::class.java, dbPerPage = 8)
    }

    override fun deleteAll() {
        super.deleteAll()
        ImagesDatabase.getInstance(context).images().deleteAll()
    }


    override fun getItemDataSource(): DataSource.Factory<Int, Image> {
        BeeLog.i(TAG, arguments)
        return arguments?.getString(ImagesPickerFragment.EXTRA_FOLDER)?.let {
            ImagesDatabase.getInstance(context).images().getAllPagedByFolder(it)
        } ?: ImagesDatabase.getInstance(context).images().getAllPaged()
    }

    override fun getItemDao(): ItemDao<Image> {
        return ImagesDatabase.getInstance(context).images()
    }

    companion object {
        private val TAG = ImageRepository::class.java.simpleName
    }
}
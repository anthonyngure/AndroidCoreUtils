package ke.co.toshngure.images.data

import android.content.Context
import android.os.Bundle
import androidx.paging.DataSource
import ke.co.toshngure.basecode.paging.data.ItemDao
import ke.co.toshngure.basecode.paging.data.ItemRepository
import ke.co.toshngure.basecode.paging.data.ItemRepositoryConfig
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.util.BaseUtils
import ke.co.toshngure.images.fragment.ImagesPickerFragment

class ImageRepository(private val context: Context) : ItemRepository<Image, Image>() {

    override fun getItemId(item: Image): Long {
        return item.id ?: BaseUtils.uuidToLong(item.path + "_" + item.bucket + "_" + item.name)
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<Image> {
        return ItemRepositoryConfig(syncClass = Image::class.java, dbPerPage = 16)
    }

    override fun deleteAll() {
        super.deleteAll()
        ImagesDatabase.getInstance(context).images().deleteAll()
    }


    override fun getItemDataSource(args: Bundle?): DataSource.Factory<Int, Image> {
        BeeLog.i(TAG, args)
        return args?.getString(ImagesPickerFragment.EXTRA_FOLDER)?.let {
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
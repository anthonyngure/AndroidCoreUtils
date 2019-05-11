package ke.co.toshngure.images.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

class ImagesViewModel(private val context: Context) : ViewModel() {

    private val mConfig = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(30)
            .setEnablePlaceholders(false)
            .build()

    private val mBucketMutableLiveData = MutableLiveData<String?>()

    val imagesList: LiveData<PagedList<Image>> = Transformations.switchMap(mBucketMutableLiveData) {

        val images = it?.let { bucket ->
            ImagesDatabase.getInstance(context).images().getAllPagedByFolder(bucket)
        } ?: ImagesDatabase.getInstance(context).images().getAllPaged()

        LivePagedListBuilder<Int, Image>(images, mConfig).build()
    }

    fun loadImages(bucket: String? = null) {
        mBucketMutableLiveData.value = bucket
    }


}

package ke.co.toshngure.images.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ke.co.toshngure.images.model.Image

class ImagesViewModel(private val context: Context) : ViewModel() {

    private val mConfig = PagedList.Config.Builder()
        .setPageSize(4)
        .setEnablePlaceholders(false)
        .build()

    private val mBucketMutableLiveData = MutableLiveData<String>()

    val imagesList: LiveData<PagedList<Image>> = Transformations.switchMap(mBucketMutableLiveData) {
        LivePagedListBuilder<Int, Image>(ImagesDataSourceFactory(it, context), mConfig).build()
    }

    fun loadImages(bucket: String? = null) {
        mBucketMutableLiveData.value = bucket
    }


}

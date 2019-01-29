package ke.co.toshngure.images.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ke.co.toshngure.images.model.Image

interface ImagesRepository {

    val images: LiveData<PagedList<Image>>

    fun toggleImageSelection(item: Image)
}
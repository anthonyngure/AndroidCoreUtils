package ke.co.toshngure.images.data

import android.content.Context
import androidx.paging.DataSource

class ImagesDataSourceFactory(private val bucket: String? = null, private val context: Context) : DataSource.Factory<Int, Image>() {
        override fun create(): DataSource<Int, Image> {
            return ImagesDataSource(bucket, context)
        }

    }
package ke.co.toshngure.images.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.PositionalDataSource
import ke.co.toshngure.images.ImagesPickerFragment
import ke.co.toshngure.images.model.Image
import java.io.File

class ImagesDataSource(private val bucket: String? = null, private val context: Context) : PositionalDataSource<Image>() {


    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Image>) {
        callback.onResult(query(params.loadSize, params.startPosition))
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Image>) {
        if (bucket == null) {
            // When not loading from a specific folder we first init all the folders
            countAndGetFolders()
            callback.onResult(query(params.requestedLoadSize, params.requestedStartPosition), 0)
        } else {
            callback.onResult(query(params.requestedLoadSize, params.requestedStartPosition), 0)
        }
    }

    private fun countAndGetFolders(): Int {
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME),
                null, null, null)
        val count = cursor?.count ?: 0
        cursor?.let { c ->
            val folders = arrayListOf<String>()
            while (c.moveToNext()) {
                val bucket = c.getString(c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                if (!folders.contains(bucket)) {
                    folders.add(bucket)
                }
            }
            val intent = Intent(ImagesPickerFragment.ACTION_FOLDERS_LOADED)
            intent.putStringArrayListExtra(ImagesPickerFragment.EXTRA_IMAGE_FOLDERS, folders)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
        cursor?.close()
        return count

    }

    @SuppressLint("Recycle")
    private fun query(limit: Int, offset: Int): MutableList<Image> {
        val images = arrayListOf<Image>()
        val cursor: Cursor? = if (bucket == null) {
            context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset)
        } else {
            context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?", arrayOf(bucket),
                MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + offset)
        }

        cursor?.let { c ->
            if (c.moveToLast()) {
                do {
                    val id = c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID))
                    val name = c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))
                    val bucket = c.getString(c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                    val file = makeSafeFile(path)
                    if (file != null && file.exists() && !file.isHidden) {
                        val image = Image(id, name, path, bucket, Uri.fromFile(file))
                        // Add to all folders
                        images.add(image)
                    }
                } while (c.moveToPrevious())

            }

            c.close()
        }
        return images
    }

    @Nullable
    private fun makeSafeFile(path: String?): File? {
        if (path == null || path.isEmpty()) {
            return null
        }
        return try {
            File(path)
        } catch (ignored: Exception) {
            null
        }

    }

    companion object {
        val projection = arrayOf(MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
    }


}
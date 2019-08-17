@file:Suppress("DEPRECATION")

package ke.co.toshngure.images.fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.app.GlideRequests
import ke.co.toshngure.basecode.app.LoadingConfig
import ke.co.toshngure.basecode.dataloading.PagingConfig
import ke.co.toshngure.basecode.dataloading.PagingFragment
import ke.co.toshngure.basecode.dataloading.adapter.BaseItemViewHolder
import ke.co.toshngure.basecode.dataloading.adapter.ItemsAdapter
import ke.co.toshngure.basecode.dataloading.util.GridSpacingItemDecoration
import ke.co.toshngure.basecode.extensions.executeAsync
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.util.BaseUtils
import ke.co.toshngure.basecode.util.PrefUtilsImpl
import ke.co.toshngure.basecode.util.Spanny
import ke.co.toshngure.images.BuildConfig
import ke.co.toshngure.images.R
import ke.co.toshngure.images.activity.CameraActivity
import ke.co.toshngure.images.data.Image
import ke.co.toshngure.images.data.ImageRepository
import ke.co.toshngure.images.data.ImagesDatabase
import ke.co.toshngure.images.view.ImageSelectionViewHolder
import ke.co.toshngure.images.view.PickedImageView
import ke.co.toshngure.images.view.PickedImageViewHolder
import kotlinx.android.synthetic.main.fragment_images_picker_top_view.*
import java.io.File
import java.util.*


@Suppress("DEPRECATION")
open class ImagesPickerFragment<D> : PagingFragment<Image, Image, D>(),
    LoaderManager.LoaderCallbacks<Cursor>,
    ItemsAdapter.OnItemClickListener<Image> {


    private val mPickedImagesListAdapter = PickedImagesListAdapter()
    private var mPickedImageList: List<Image> = listOf()
    private var mDoneMenuItem: MenuItem? = null
    private lateinit var mGlideRequests: GlideRequests
    private lateinit var mPrefUtils: PrefUtilsImpl
    private lateinit var mCropingImage: Image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGlideRequests = GlideApp.with(this)
        mPrefUtils = PrefUtilsImpl(context!!, PreferenceManager.getDefaultSharedPreferences(context))
        executeAsync {
            ImagesDatabase.getInstance(context!!).images().clearAllSelection()
        }
    }

    override fun getPagingConfig(): PagingConfig<Image, Image> {
        return PagingConfig(
            layoutRes = R.layout.item_image_selection,
            repository = ImageRepository(activity!!),
            diffUtilItemCallback = Image.DIFF_UTIL_ITEM_CALLBACK,
            itemClickListener = this,
            withDivider = false,
            itemAnimator = null
        )
    }

    override fun getLoadingConfig(): LoadingConfig {
        return LoadingConfig(showNoDataLayout = false)
    }

    override fun createItemViewHolder(itemView: View): BaseItemViewHolder<Image> {
        return ImageSelectionViewHolder(itemView, mGlideRequests)
    }

    override fun onClick(item: Image) {
        // When only one image is selectable
        // Un select all first and select the clicked one
        when (val maxImages = maximumImages()) {
            1 -> SelectAndCompressTask(this::getContext, maxImages).execute(item)
            // If it is a selection and the maximum has been reached, alert the user
            // It is a selection if image is not selected
            mPickedImagesListAdapter.itemCount -> showErrorSnack("Limit is $maxImages images")
            // Selection max has not been reached yet
            else -> {
                if (item.selected) {
                    executeAsync { ImagesDatabase.getInstance(activity!!).images().update(item.copy(selected = false)) }
                } else {
                    SelectAndCompressTask(this::getContext, maxImages).execute(item)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        activity?.let {
            executeAsync { ImagesDatabase.getInstance(it).images().deleteAll() }
        }
    }

    override fun onSetUpCollapsibleView(container: FrameLayout) {
        super.onSetUpCollapsibleView(container)
        layoutInflater.inflate(R.layout.fragment_images_picker_top_view, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        pickedImagesRV.layoutManager = layoutManager
        pickedImagesRV.adapter = mPickedImagesListAdapter

        cameraIV.setOnClickListener {
            val intent = Intent(context, CameraActivity::class.java)
            startActivityWithPermissionsCheck(
                intent, CAMERA_REQUEST_CODE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        foldersMS.setOnItemSelectedListener { _, _, _, item ->
            toastDebug(item)
            if (item == ALL_IMAGES_FOLDER_NAME) {
                loadWithArgs(null)
            } else {
                val args = Bundle()
                args.putString(EXTRA_FOLDER, item.toString())
                loadWithArgs(args)
            }
        }
    }

    override fun onSetUpRecyclerView(recyclerView: RecyclerView) {
        super.onSetUpRecyclerView(recyclerView)
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(GridSpacingItemDecoration(4, 2, false))
        }
    }


    override fun onStart() {
        super.onStart()
        LoaderManager.enableDebugLogging(BeeLog.DEBUG)
        LoaderManager.getInstance(this).initLoader(100, null, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*Folders*/
        ImagesDatabase.getInstance(activity!!).images().getFolderList().observe(this, Observer {
            foldersMS?.let { spinner ->
                val folderNames = arrayListOf<String>()
                folderNames.addAll(it)
                folderNames.add(ALL_IMAGES_FOLDER_NAME)
                folderNames.sortBy { folder -> folder }
                spinner.setItems(folderNames)
                spinner.selectedIndex = folderNames.indexOf(ALL_IMAGES_FOLDER_NAME)
            }
        })

        /* Selected Images*/
        ImagesDatabase.getInstance(activity!!).images().getAllSelected().observe(this, Observer {

            mPickedImageList = it

            mPickedImagesListAdapter.notifyDataSetChanged()

            if (mPickedImageList.size > 1) {
                pickedImagesRV.smoothScrollToPosition(mPickedImageList.size - 1)
            }

            mDoneMenuItem?.isVisible = mPickedImageList.isNotEmpty()

            selectionCountTV.text = Spanny("${mPickedImageList.size}/${maximumImages()} selected")
        })
    }

    override fun onShowLoading(loadingLayout: LinearLayout?, contentViewContainer: FrameLayout?) {
        super.onShowLoading(loadingLayout, contentViewContainer)
        mDoneMenuItem?.isVisible = false
    }

    override fun onHideLoading(loadingLayout: LinearLayout?, contentViewContainer: FrameLayout?) {
        super.onHideLoading(loadingLayout, contentViewContainer)
        mDoneMenuItem?.isVisible = true
    }

    protected open fun onDoneClicked(selection: List<Image>) {}

    protected open fun onSkipClicked() {}

    protected open fun skipButtonEnable(): Boolean {
        return false
    }

    protected open fun maximumImages(): Int {
        return 1
    }

    protected open fun multipleImages(): Boolean {
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        toastDebug("onActivityResult, $requestCode")
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            // CAMERA
            data?.let {
                val image = it.getParcelableExtra<Image>(CameraActivity.EXTRA_IMAGE)
                BeeLog.i(TAG, image)
                SelectAndCompressTask(this::getContext, maximumImages()).execute(image)
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == CROP_REQUEST_CODE) {
            // CROP
            data?.let {
                val resultUri = UCrop.getOutput(it)
                executeAsync {
                    ImagesDatabase.getInstance(activity!!).images()
                        .update(mCropingImage.copy(croppedPath = resultUri?.path))
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            data?.let {
                toastDebug(UCrop.getError(it)?.message.toString())
            }
            showErrorSnack("Unable to crop image")
        }
    }

    private fun setSelectionTitle(title: String?) {
        selectionCountTV.text = title
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_picker_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_skip)?.isVisible = skipButtonEnable()
        mDoneMenuItem = menu.findItem(R.id.action_done)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                mDoneMenuItem = item
                onDoneClicked(mPickedImageList)
                false
            }
            R.id.action_skip -> {
                onSkipClicked()
                false
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private inner class PickedImagesListAdapter : RecyclerView.Adapter<PickedImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedImageViewHolder {
            return PickedImageViewHolder(PickedImageView(parent.context))
        }

        override fun getItemCount(): Int {
            return mPickedImageList.size
        }

        override fun onBindViewHolder(holder: PickedImageViewHolder, position: Int) {
            val pickedImageView = holder.itemView as PickedImageView
            val image = mPickedImageList[position]
            pickedImageView.setImage(
                image, this@ImagesPickerFragment,
                mPrefUtils, this@ImagesPickerFragment::cropImage
            )
        }

    }

    private fun cropImage(image: Image) {
        mCropingImage = image
        val destinationUri = Uri.fromFile(File(context?.cacheDir, "cropped_" + System.currentTimeMillis() + ".jpg"))
        context?.let { ctx ->
            val options = UCrop.Options()
            options.setToolbarWidgetColor(ContextCompat.getColor(ctx, android.R.color.white))
            options.setStatusBarColor(BaseUtils.getColor(ctx, R.attr.colorPrimaryDark))
            options.setToolbarColor(BaseUtils.getColor(ctx, R.attr.colorPrimary))
            options.setToolbarCropDrawable(R.drawable.ic_done_black_24dp)
            options.setToolbarCancelDrawable(R.drawable.ic_clear_black_24dp)
            options.setShowCropFrame(true)
            options.setShowCropGrid(true)
            UCrop.of(Uri.fromFile(File(image.resolvePath().toString())), destinationUri)
                .withOptions(options)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(640, 480)
                .start(ctx, this, CROP_REQUEST_CODE)
        }
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        return CursorLoader(
            activity!!, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
            null, null, MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT 1008"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) = data?.let { c ->
        if (!c.isClosed) {
            activity?.let {
                executeAsync {
                    val images = arrayListOf<Image>()
                    if (c.moveToLast()) {
                        do {
                            //val id = c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID))
                            val name = c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                            val path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))
                            val bucket = c.getString(c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                            val dateAdded = c.getLong(c.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))

                            val file = makeSafeFile(path)
                            if (file != null && file.exists() && !file.isHidden) {
                                val image = Image(
                                    name = name, path = path, sizeInBytes = file.length(),
                                    bucket = bucket, dateAdded = dateAdded
                                )
                                //Add to all folders
                                images.add(image)

                            }
                        } while (c.moveToPrevious())
                    }
                    ImagesDatabase.getInstance(it).images().insert(images)
                    c.close()
                }
            }

        }
    } ?: BeeLog.i(TAG, "Cursor is null")

    override fun onLoaderReset(loader: Loader<Cursor>) {
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

    @Suppress("DEPRECATION")
    class SelectAndCompressTask(private val getContext: () -> Context?, private val maxImages: Int = 1) :
        AsyncTask<Image, Void, Image>() {

        private var mProgressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            mProgressDialog = ProgressDialog(getContext())
            mProgressDialog?.setMessage(getContext()?.getString(R.string.message_waiting))
            mProgressDialog?.show()
        }

        override fun doInBackground(vararg params: Image?): Image {
            val image = params[0]!!

            BeeLog.i(TAG, image)

            val actualImage = File(image.path)

            val sizeInKB = if (image.sizeInBytes > 0) {
                image.sizeInBytes / 1024
            } else {
                0
            }
            BeeLog.i(TAG, "IMAGE SIZE IN KBS = $sizeInKB")

            if (maxImages == 1) {
                ImagesDatabase.getInstance(getContext()!!).images().clearAllSelection()
            }

            if (sizeInKB > 75 && image.compressedPath.isNullOrEmpty()) {
                val compressedImage = Compressor(getContext())
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(85)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(getContext()?.cacheDir?.absolutePath)
                    .compressToFile(actualImage)
                val imageUpdate = image.copy(
                    selected = true,
                    selectedAt = Calendar.getInstance().timeInMillis,
                    sizeInBytes = compressedImage.length(),
                    compressedPath = compressedImage.path
                )
                ImagesDatabase.getInstance(getContext()!!).images().insert(imageUpdate)
                return imageUpdate
            } else {
                val imageUpdate = image.copy(
                    selected = true,
                    selectedAt = Calendar.getInstance().timeInMillis
                )
                ImagesDatabase.getInstance(getContext()!!).images().insert(imageUpdate)
                return imageUpdate
            }


        }

        override fun onPostExecute(result: Image?) {
            super.onPostExecute(result)
            mProgressDialog?.dismiss()
        }

    }


    companion object {
        const val ALL_IMAGES_FOLDER_NAME = "Gallery"
        const val EXTRA_FOLDER = "${BuildConfig.APPLICATION_ID}.extra_folder"
        private const val CAMERA_REQUEST_CODE = 150
        const val CROP_REQUEST_CODE = 350
    }

}

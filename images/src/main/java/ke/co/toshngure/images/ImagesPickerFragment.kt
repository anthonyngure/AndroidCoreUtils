package ke.co.toshngure.images

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yalantis.ucrop.UCrop
import ke.co.toshngure.basecode.app.BaseAppFragment
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.app.GlideRequests
import ke.co.toshngure.basecode.dataloading.util.GridSpacingItemDecoration
import ke.co.toshngure.basecode.util.Spanny
import ke.co.toshngure.images.activity.CameraActivity
import ke.co.toshngure.images.data.Image
import ke.co.toshngure.images.data.ImagesViewModel
import ke.co.toshngure.views.media.NetworkImage
import kotlinx.android.synthetic.main.fragment_images_picker.*


open class ImagesPickerFragment<D> : BaseAppFragment<D>() {

    private val mPickedImagesListAdapter = PickedImagesListAdapter()
    private lateinit var mImageViewModel: ImagesViewModel
    private lateinit var mImagesListAdapter: ImagesListAdapter
    private val mPickedImageList: MutableList<Image> = mutableListOf()
    private lateinit var mGlideRequests: GlideRequests
    private var mDoneMenuItem: MenuItem? = null

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (intent.action == ACTION_FOLDERS_LOADED) {
                    foldersMS?.let { spinner ->
                        val folderNames = it.getStringArrayListExtra(EXTRA_IMAGE_FOLDERS)
                        folderNames.add(ALL_IMAGES_FOLDER_NAME)
                        folderNames.sortBy { folder -> folder }
                        spinner.setItems(folderNames)
                        spinner.selectedIndex = folderNames.indexOf(ALL_IMAGES_FOLDER_NAME)
                    }
                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mGlideRequests = GlideApp.with(this)
        setHasOptionsMenu(true)
        context.let {
            val filter = IntentFilter(ACTION_FOLDERS_LOADED)
            LocalBroadcastManager.getInstance(it).registerReceiver(mBroadcastReceiver, filter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(mBroadcastReceiver)
        }
    }


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        LayoutInflater.from(container.context).inflate(R.layout.fragment_images_picker, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        pickedImagesRV.layoutManager = layoutManager
        pickedImagesRV.adapter = mPickedImagesListAdapter

        val imagesLayoutManager = GridLayoutManager(imagesRV.context, 4)
        imagesRV.layoutManager = imagesLayoutManager
        imagesRV.addItemDecoration(GridSpacingItemDecoration(4, 2, false))
        val diffCallback = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.selected && newItem.selected
            }

        }
        mImagesListAdapter = ImagesListAdapter(diffCallback)
        imagesRV.adapter = mImagesListAdapter


        cameraIV.setOnClickListener {
            val intent = Intent(context, CameraActivity::class.java)
            startActivityWithPermissionsCheck(intent, maximumImages() + 1, Manifest.permission.CAMERA)
        }

        foldersMS.setOnItemSelectedListener { _, _, _, item ->
            toastDebug(item)
            if (item == ALL_IMAGES_FOLDER_NAME) {
                mImageViewModel.loadImages()
            } else {
                mImageViewModel.loadImages(item.toString())
            }
        }


    }

    private fun getViewModel(): ImagesViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <M : ViewModel?> create(modelClass: Class<M>): M {
                @Suppress("UNCHECKED_CAST")
                return ImagesViewModel(activity!!) as M
            }
        })[ImagesViewModel::class.java]
    }


    override fun onStart() {
        super.onStart()

        mImageViewModel = getViewModel()

        mImageViewModel.imagesList.observe(this, Observer { list ->
            mImagesListAdapter.submitList(list)
            imagesProgressBar.visibility = if (mImagesListAdapter.itemCount > 0) View.GONE else View.VISIBLE
        })

        mImageViewModel.loadImages()
    }


    protected open fun onDoneClicked(selection: MutableList<Image>) {}

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
        toastDebug("onActivityResult")
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                val resultUri = UCrop.getOutput(it)
                for (image in mPickedImageList) {
                    if (image.displayPosition == requestCode) {
                        image.croppedUri = resultUri
                        image.croppedPath = resultUri?.path
                    }
                }
                mPickedImagesListAdapter.notifyDataSetChanged()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            data?.let {
                // val cropError = UCrop.getError(it)
            }
        }
    }

    private fun setTitle(title: String?) {
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

    private fun removePickedImage(image: Image) {
        mPickedImageList.remove(image)
        mDoneMenuItem?.isVisible = mPickedImageList.size > 0
        mPickedImagesListAdapter.notifyDataSetChanged()
    }

    private inner class PickedImagesListAdapter :
        RecyclerView.Adapter<PickedImagesListAdapter.PickedImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedImageViewHolder {
            return PickedImageViewHolder(PickedImageView(parent.context))
        }

        override fun getItemCount(): Int {
            return mPickedImageList.size
        }

        override fun onBindViewHolder(holder: PickedImageViewHolder, position: Int) {
            val pickedImageView = holder.itemView as PickedImageView
            pickedImageView.setImage(
                mPickedImageList[position], mGlideRequests,
                this@ImagesPickerFragment, position,
                this@ImagesPickerFragment::removePickedImage
            )
        }

        /**
         * @param image selected or unselected image
         * We first remove the image if it exists in the selected list
         * if image is selected we add it to the list
         */
        fun handle(image: Image) {
            val indexInPickedImages = mPickedImageList.indexOfFirst { picked -> picked.id == image.id }
            if (indexInPickedImages > -1) mPickedImageList.removeAt(indexInPickedImages)

            if (image.selected) {
                mPickedImageList.add(image)
                notifyDataSetChanged()
            }

            if (mPickedImageList.size > 1) {
                pickedImagesRV.smoothScrollToPosition(mPickedImageList.size - 1)
            }
            mDoneMenuItem?.isVisible = mPickedImageList.size > 0
            selectionCountTV.text = Spanny("${mPickedImageList.size}/${maximumImages()} selected")
        }

        fun clear() {
            mPickedImageList.clear()
            notifyDataSetChanged()
            mDoneMenuItem?.isVisible = false
        }

        private inner class PickedImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }


    private inner class ImagesListAdapter(diffCallback: DiffUtil.ItemCallback<Image>) :
        PagedListAdapter<Image, ImagesListAdapter.ImageSelectionViewHolder>(diffCallback) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectionViewHolder {
            val view = layoutInflater.inflate(R.layout.item_image_selection, parent, false)
            return ImageSelectionViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageSelectionViewHolder, position: Int) {
            val item = getItem(position)
            item?.let { image ->
                holder.imageNI.loadImageFromMediaStore(image.path, mGlideRequests)
                holder.backgroundView.visibility = if (image.selected) View.VISIBLE else View.GONE
                holder.selectionIV.visibility = if (image.selected) View.VISIBLE else View.GONE
                holder.itemView.setOnClickListener {
                    toggleImageSelection(image, position)
                }
            }

        }

        override fun onBindViewHolder(holder: ImageSelectionViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            val item = getItem(position)
            item?.let { image ->
                holder.backgroundView.visibility = if (image.selected) View.VISIBLE else View.GONE
                holder.selectionIV.visibility = if (image.selected) View.VISIBLE else View.GONE
            }
        }

        private fun toggleImageSelection(image: Image, position: Int) {
            val maxImages = maximumImages()
            // When only one image is selectable
            // Un select all first and select the clicked one
            if (maxImages == 1) {
                // this.imageList.forEach { it.selected = false }
                image.selected = true
                mPickedImagesListAdapter.clear()
                mPickedImagesListAdapter.handle(image)
            }
            // If it is a selection and the maximum has been reached, alert the user
            // It is a selection if image is not selected
            else if (!image.selected && mPickedImagesListAdapter.itemCount == maxImages) {
                showErrorSnack("Limit is $maxImages images")
            }
            // Selection max has not been reached yet
            else {
                image.selected = !image.selected
                mPickedImagesListAdapter.handle(image)
                mImagesListAdapter.notifyItemChanged(position, image)
            }
        }

        private inner class ImageSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageNI: NetworkImage = itemView.findViewById(R.id.imageNI)
            val backgroundView: View = itemView.findViewById(R.id.backgroundView)
            val selectionIV: ImageView = itemView.findViewById(R.id.selectionIV)
        }

    }

    companion object {

        const val ACTION_FOLDERS_LOADED = "${BuildConfig.APPLICATION_ID} ACTION_FOLDERS_LOADED"
        const val ALL_IMAGES_FOLDER_NAME = "Gallery"
        const val EXTRA_IMAGE_FOLDERS = "extra_image_folders"
    }

}

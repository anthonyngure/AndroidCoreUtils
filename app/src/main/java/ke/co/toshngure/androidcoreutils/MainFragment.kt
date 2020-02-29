package ke.co.toshngure.androidcoreutils

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import ke.co.toshngure.basecode.app.BaseAppFragment
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase
import ke.co.toshngure.extensions.executeAsync
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseAppFragment<Any>() {


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        layoutInflater.inflate(R.layout.fragment_main, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        usersBtn.setOnClickListener { navigateWithPermissionsCheck(R.id.usersFragment) }

        postsBtn.setOnClickListener { navigateWithPermissionsCheck(R.id.postsFragment) }

        albumsBtn.setOnClickListener { navigateWithPermissionsCheck(R.id.albumsFragment) }

        imagesPickerBtn.setOnClickListener {
            navigateWithPermissionsCheck(
                R.id.testImagesPickerFragment, null,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        smsRetrieverBtn.setOnClickListener { navigateWithPermissionsCheck(R.id.smsRetrieverFragment) }

        clearFab.setOnClickListener {
            executeAsync {

                AppDatabase.getInstance().clearAllTables()

                SyncStatesDatabase.getInstance().clearAllTables()
            }
        }

        gpxBtn.setOnClickListener {
            navigateWithPermissionsCheck(
                R.id.gpxFragment, null,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        cacheTestBtn.setOnClickListener { navigateWithPermissionsCheck(R.id.cacheTestFragment) }
        sharedPrefsBtn.setOnClickListener { navigateWithPermissionsCheck(R.id.sharedPrefsFragment) }
    }

    companion object {
        const val TAG = "MainFragment"
    }

}

package ke.co.toshngure.geo

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import ke.co.toshngure.extensions.executeAsync
import kotlinx.android.synthetic.main.activity_location_picker.*
import java.util.*


class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener, LocationInputView.OnLocationChangedListener {

    private var mGoogleMap: GoogleMap? = null
    private var mCameraUpdates: Int = 0
    private lateinit var mSelectedLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
        mapFragment?.let {
            val fragment = it as SupportMapFragment
            fragment.getMapAsync(this)
        }

        locationInputView.setOnLocationChangedListener(this)

        confirmButton.setOnClickListener {
            val data = Intent()
            data.putExtra(EXTRA_LOCATION, mSelectedLocation)
            setResult(Activity.RESULT_OK, data)
            this.finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap

        mGoogleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        mGoogleMap?.setOnCameraIdleListener(this)


        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    // Show last known location
                    showLocation(LatLng(it.latitude, it.longitude))
                }
            }

    }

    private fun showLocation(latLng: LatLng) {

        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(14f)
            .build()

        mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        fetchAddress(latLng)
    }

    override fun onCameraIdle() {
        mCameraUpdates++

        if (mCameraUpdates == 1) {
            return
        }

        mGoogleMap?.cameraPosition?.target?.let {
            fetchAddress(it)
        }

    }

    private fun fetchAddress(latLng: LatLng) {
        confirmButton.isEnabled = false
        confirmButton.isCheckable = false
        executeAsync({
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            } catch (ex: Exception) {
                Log.e(TAG, ex.toString())
                null
            }
        }, { addresses ->
            addresses?.let {
                if (it.size > 0) {
                    val address = it[0]
                    val location =
                        Location(address.latitude, address.longitude, address.getAddressLine(0))
                    locationInputView.setLocation(location)
                }
            }
        })
    }

    override fun onLocationChanged(location: Location) {
        this.mSelectedLocation = location
        confirmButton.isEnabled = true
        confirmButton.isCheckable = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        this.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_LOCATION = "extra_location"
        val TAG = LocationPickerActivity::class.java.simpleName
    }

}

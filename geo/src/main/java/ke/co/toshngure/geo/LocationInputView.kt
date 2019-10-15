package ke.co.toshngure.geo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.RecyclerViewPresenter
import ke.co.toshngure.extensions.hide
import ke.co.toshngure.extensions.show
import kotlinx.android.synthetic.main.view_location_input.view.*


/**
 * Created by Anthony Ngure on 6/6/2019
 * @author Anthony Ngure
 */

class LocationInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var locationAutocomplete: Autocomplete<AutocompletePrediction>
    private var mLocation: Location? = null
    private var onLocationChangedListener: OnLocationChangedListener? = null
    private var mPickLocationRequestCode = 100
    private lateinit var mMapsApiKey: String

    init {

        LayoutInflater.from(context).inflate(R.layout.view_location_input, this, true)

        mapButton.hide()

        locationAutocomplete = Autocomplete.on<AutocompletePrediction>(locationET)
            .with(6f)
            .with(ColorDrawable(Color.WHITE))
            .with(LocationPredictionPresenter(locationET.context))
            .with(object : AutocompleteCallback<AutocompletePrediction> {
                override fun onPopupItemClicked(
                    editable: Editable,
                    item: AutocompletePrediction
                ): Boolean {
                    editable.clear()
                    editable.append(item.getPrimaryText(null))
                    // locationMET.setSelection(0);
                    fetchLocationDetails(item)
                    return true
                }

                override fun onPopupVisibilityChanged(shown: Boolean) {

                }
            }).build()

    }

    private fun fetchLocationDetails(item: AutocompletePrediction) {

        // Specify the fields to return.
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.builder(item.placeId, placeFields)
            .build()

        // Create a new Places client instance.
        val placesClient = Places.createClient(context)

        placesClient.fetchPlace(request).addOnSuccessListener { fetchPlaceResponse ->

            Log.i(TAG, fetchPlaceResponse.toString())

            this.mLocation = Location()
            this.mLocation?.latitude =
                fetchPlaceResponse.place.latLng?.latitude ?: 0.0
            this.mLocation?.longitude =
                fetchPlaceResponse.place.latLng?.longitude ?: 0.0
            this.mLocation?.name = fetchPlaceResponse.place.name ?: ""

            this.mLocation?.let {
                this.onLocationChangedListener?.onLocationChanged(it)
            }


        }.addOnFailureListener { exception ->
            Log.e(TAG, exception.message.toString())
        }
    }

    private class LocationPredictionPresenter(context: Context) :
        RecyclerViewPresenter<AutocompletePrediction>(context) {


        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()

        // Create a new Places client instance.
        var placesClient = Places.createClient(context)


        // Create a RectangularBounds object.
        var bounds = RectangularBounds.newInstance(
            LatLng(-33.880490, 151.184363),
            LatLng(-33.858754, 151.229596)
        )

        // Use the builder to create a FindAutocompletePredictionsRequest.
        var requestBuilder = FindAutocompletePredictionsRequest.builder()
            // Call either setLocationBias() OR setLocationRestriction().
            //.setLocationBias(bounds)
            //.setLocationRestriction(bounds)
            .setCountry("KE")
            // .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)
        // .setQuery(query)

        private lateinit var mAdapter: LocationPredictionAdapter


        override fun onQuery(query: CharSequence?) {
            Log.i(TAG, "Starting autocomplete query for: $query")
            // Query the autocomplete API for the (query) search string.
            query?.let {

                placesClient.findAutocompletePredictions(requestBuilder.setQuery(it.toString()).build())
                    .addOnSuccessListener { predictionsResponse ->
                        mAdapter.setData(predictionsResponse.autocompletePredictions)
                        mAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, exception.message.toString())
                    }
            }

        }

        override fun instantiateAdapter(): RecyclerView.Adapter<*> {
            mAdapter = LocationPredictionAdapter()
            return mAdapter
        }

        private inner class LocationPredictionAdapter :
            RecyclerView.Adapter<LocationPredictionAdapter.LocationPredictionViewHolder>() {

            /**
             * Current results returned by this adapter.
             */
            private var data: List<AutocompletePrediction>? = null

            private fun isEmpty(): Boolean {
                return data.isNullOrEmpty()
            }


            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): LocationPredictionViewHolder {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_autocomplete_prediction, parent, false)
                return LocationPredictionViewHolder(view)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: LocationPredictionViewHolder, position: Int) {
                if (isEmpty()) {
                    holder.titleTV.text = "No results"
                    holder.subTitleTV.text = "Please try typing something else."
                    holder.itemView.setOnClickListener(null)
                    return
                }
                val prediction = data!![position]
                holder.titleTV.text = prediction.getPrimaryText(null).toString()
                holder.subTitleTV.text = prediction.getSecondaryText(null).toString()
                holder.itemView.setOnClickListener { dispatchClick(prediction) }
            }

            fun setData(data: List<AutocompletePrediction>) {
                this.data = data
            }

            override fun getItemCount(): Int {
                return if (isEmpty()) 1 else data!!.size
            }

            inner class LocationPredictionViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {

                val titleTV: TextView = itemView.findViewById(R.id.titleTV)
                val subTitleTV: TextView = itemView.findViewById(R.id.subTitleTV)

            }
        }


    }

    fun setOnLocationChangedListener(locationChangedListener: OnLocationChangedListener) {
        this.onLocationChangedListener = locationChangedListener
    }

    @Nullable
    fun getLocation(): Location? {
        return this.mLocation
    }

    fun setError(error: String?) {
        locationTIL.error = error
    }


    fun setHint(hint: String?): LocationInputView {
        locationET.hint = hint
        locationTIL.hint = hint
        return this
    }

    fun setLocation(location: Location) {
        this.locationAutocomplete.setEnabled(false)
        locationET.setText(location.name)
        locationET.setSelection(location.name?.length ?: 0)
        this.locationAutocomplete.setEnabled(true)
        this.mLocation = location
        this.mLocation?.let {
            this.onLocationChangedListener?.onLocationChanged(it)
        }
    }

    fun  initLocationPicker(fragment: Fragment, mapsApiKey: String, requestCode: Int = 100): LocationInputView {
        this.mMapsApiKey = mapsApiKey
        this.mPickLocationRequestCode = requestCode
        mapButton.show()
        mapButton.setOnClickListener {
            fragment.startActivityForResult(Intent(context, LocationPickerActivity::class.java), mPickLocationRequestCode)
        }
        return this
    }

    fun initLocationPicker(activity: FragmentActivity, mapsApiKey: String, requestCode: Int = 100): LocationInputView {
        this.mMapsApiKey = mapsApiKey
        this.mPickLocationRequestCode = requestCode
        mapButton.show()
        mapButton.setOnClickListener {
            activity.startActivityForResult(Intent(context, LocationPickerActivity::class.java), mPickLocationRequestCode)
        }
        return this
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == mPickLocationRequestCode && resultCode == Activity.RESULT_OK) {
            val location = data.getParcelableExtra<Location>(LocationPickerActivity.EXTRA_LOCATION)
            location?.let {
                setLocation(it)
            }
        }
    }

    interface OnLocationChangedListener {
        fun onLocationChanged(location: Location)
    }

    companion object {
        private const val TAG = "PlacesSuggestions"
    }
}
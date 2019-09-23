package ke.co.toshngure.geo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var name: String? = ""
) : Parcelable
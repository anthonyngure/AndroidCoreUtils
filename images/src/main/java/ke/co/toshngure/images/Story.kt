package ke.co.toshngure.images

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Story(val url: String, val text: String? = null) : Parcelable

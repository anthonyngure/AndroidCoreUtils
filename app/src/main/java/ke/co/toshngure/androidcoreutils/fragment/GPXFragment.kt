package ke.co.toshngure.androidcoreutils.fragment

import android.os.Bundle
import android.view.View
import com.google.android.gms.location.LocationServices
import ke.co.toshngure.basecode.app.BaseAppFragment

/**
 * Created by Anthony Ngure on 9/24/2019
 *
 * @author Anthony Ngure
 */
class GPXFragment : BaseAppFragment<Any>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates()
    }
}

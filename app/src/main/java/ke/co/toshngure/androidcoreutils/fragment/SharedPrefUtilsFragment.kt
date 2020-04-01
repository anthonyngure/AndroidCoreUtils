package ke.co.toshngure.androidcoreutils.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.androidcoreutils.users.User
import ke.co.toshngure.basecode.util.PrefUtils
import kotlinx.android.synthetic.main.fragment_shared_pref_utils.*

class SharedPrefUtilsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shared_pref_utils, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener {
            val user = User(id = 1, name = "Anthony")
            PrefUtils.getInstance().saveItem(R.string.pref_user, user)
            val savedUser : User? = PrefUtils.getInstance().getItem(R.string.pref_user)
            //PrefUtils.getInstance().getItem(R.string.pref_user)
            textTV.text = savedUser.toString()
        }
    }

}

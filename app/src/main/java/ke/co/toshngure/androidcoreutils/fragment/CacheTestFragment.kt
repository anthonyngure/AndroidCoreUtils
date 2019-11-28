package ke.co.toshngure.androidcoreutils.fragment

import android.widget.FrameLayout
import com.google.gson.Gson
import ke.co.toshngure.androidcoreutils.ApiService
import ke.co.toshngure.androidcoreutils.R
import ke.co.toshngure.androidcoreutils.posts.Post
import ke.co.toshngure.basecode.app.BaseAppFragment
import kotlinx.android.synthetic.main.fragment_cache_test.*
import retrofit2.Call

class CacheTestFragment : BaseAppFragment<Post>() {


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        layoutInflater.inflate(R.layout.fragment_cache_test, container, true)
    }

    override fun onStart() {
        super.onStart()
        makeRequest()
    }

    override fun onDataReady(data: Post) {
        super.onDataReady(data)
        textView.text = Gson().toJson(data)
    }

    override fun getApiCall(): Call<Post>? {
        return ApiService.getTypicodeInstance().post(1)
    }
}

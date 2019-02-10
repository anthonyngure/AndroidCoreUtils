package ke.co.toshngure.basecode.app


import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import ke.co.toshngure.basecode.R
import kotlinx.android.synthetic.main.fragment_tabs.*


abstract class TabsFragment<M> : BaseAppFragment<M>() {


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        LayoutInflater.from(container.context).inflate(R.layout.fragment_tabs, container, true)
        viewPager.adapter = getSectionsPagerAdapter()
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = getTabMode()
    }
    
    abstract fun getSectionsPagerAdapter() : FragmentStatePagerAdapter


    protected open fun getTabMode() : Int {
        return TabLayout.MODE_FIXED
    }

}

package ke.co.toshngure.basecode.app


import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import ke.co.toshngure.basecode.R
import kotlinx.android.synthetic.main.basecode_fragment_tabs.*


abstract class TabsFragment<M> : BaseAppFragment<M>() {


    override fun onSetUpContentView(container: FrameLayout) {
        super.onSetUpContentView(container)
        LayoutInflater.from(container.context).inflate(R.layout.basecode_fragment_tabs, container, true)
        viewPager.adapter = getSectionsPagerAdapter()
        onSetUpTabLayout(tabLayout)
    }


    protected open fun onSetUpTabLayout(tabLayout: TabLayout) {
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = getTabMode()
    }

    abstract fun getSectionsPagerAdapter(): FragmentStatePagerAdapter


    protected open fun getTabMode(): Int {
        return TabLayout.MODE_FIXED
    }

}

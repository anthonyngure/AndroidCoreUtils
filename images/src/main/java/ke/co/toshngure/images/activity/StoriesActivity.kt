package ke.co.toshngure.images.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import jp.shts.android.storiesprogressview.StoriesProgressView
import ke.co.toshngure.basecode.app.BaseAppActivity
import ke.co.toshngure.basecode.app.GlideApp
import ke.co.toshngure.basecode.util.BaseUtils
import ke.co.toshngure.basecode.util.PrefUtilsImpl
import ke.co.toshngure.images.R
import ke.co.toshngure.images.Story
import kotlinx.android.synthetic.main.activity_stories.*

class StoriesActivity : BaseAppActivity(), StoriesProgressView.StoriesListener {


    private lateinit var mPrefUtils: PrefUtilsImpl
    private var counter = 0
    private var pressTime = 0L
    private var limit = 500L
    private lateinit var urls: ArrayList<Story>

    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView?.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView?.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_stories)

        urls = intent.extras?.getParcelableArrayList(EXTRA_STORIES) ?: arrayListOf()
        counter = intent.extras?.getInt(EXTRA_START, 0) ?: 0

        storiesProgressView?.setStoriesListener(this)
        storiesProgressView?.setStoriesCount(urls.size)
        storiesProgressView?.setStoryDuration(3000L)

        // or
        //storiesProgressView?.setStoriesCountWithDurations(durations);
        //storiesProgressView.startStories();


        // bind reverse view
        reverse.setOnClickListener { storiesProgressView?.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        // bind skip view
        skip.setOnClickListener { storiesProgressView?.skip() }
        skip.setOnTouchListener(onTouchListener)

        start()

        /*mPrefUtils = PrefUtilsImpl(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (!mPrefUtils.getBoolean(R.string.pref_learnt_viewing_stories, false)) {
            showHowToViewStories()
        } else {
            start()
        }*/
    }

    private fun start() {
        storiesProgressView?.startStories(counter)
        imageNI?.loadImageFromNetwork(urls[counter].url, GlideApp.with(this))
    }

    private fun showHowToViewStories() {
        TapTargetSequence(this)
            .target(
                TapTarget.forView(reverse, "View Previous", "Tap to view previous")
                    .outerCircleAlpha(0.8f)
                    .outerCircleColorInt(BaseUtils.getColor(this, R.attr.colorPrimary))
                    .transparentTarget(true)
                    .cancelable(true)
            )
            .target(
                TapTarget.forView(skip, "View Next", "Tap to skip")
                    .outerCircleAlpha(0.8f)
                    .outerCircleColorInt(BaseUtils.getColor(this, R.attr.colorPrimary))
                    .transparentTarget(true)
                    .cancelable(true)
            )
            .listener(object : TapTargetSequence.Listener {

                override fun onSequenceCanceled(lastTarget: TapTarget?) {

                }

                override fun onSequenceFinish() {
                    mPrefUtils.writeBoolean(R.string.pref_learnt_viewing_stories, true)
                    start()
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

                }

            }).start()
    }

    override fun onNext() {
        imageNI?.loadImageFromNetwork(urls[++counter].url, GlideApp.with(this))
    }


    override fun onPrev() {
        if (counter - 1 < 0) return
        imageNI?.loadImageFromNetwork(urls[--counter].url, GlideApp.with(this))
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        // Very important !
        storiesProgressView?.destroy()
        super.onDestroy()
    }


    companion object {

        private const val EXTRA_STORIES = "extra_stories"
        private const val EXTRA_START = "extra_start"

        fun start(context: Context, stories: ArrayList<Story>, start: Int = 0) {
            val intent = Intent(context, StoriesActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_STORIES, stories)
            intent.putExtra(EXTRA_START, start)
            if (stories.size > 0 && start < stories.size) {
                context.startActivity(intent)
            }
        }
    }

}

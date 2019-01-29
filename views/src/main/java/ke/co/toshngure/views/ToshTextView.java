/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorRes;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ke.co.toshngure.views.utils.VectorUtils;


/**
 * Created by Tosh on 7/11/16.
 */

public class ToshTextView extends AppCompatTextView {

    public static final String TAG = ToshTextView.class.getSimpleName();
    public static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_-]+)");
    public static final Pattern HASHTAG_PATTERN = Pattern.compile("#([A-Za-z0-9_-]+)");
    public static final Pattern WEB_PATTERN = Patterns.WEB_URL;
    public static final int MENTION = 0;
    public static final int HASHTAG = 1;
    public static final int WEB = 2;
    private static final long INITIAL_UPDATE_INTERVAL = DateUtils.MINUTE_IN_MILLIS;

    private long mReferenceTime;
    private Handler mHandler = new Handler();
    private UpdateTimeRunnable mUpdateTimeTask;
    private boolean isUpdateTaskRunning = false;
    private boolean isUsingReferenceTime = false;
    @ColorRes
    private int mLinkColor;
    private String mPrefix;

    public ToshTextView(Context context) {
        this(context, null);
    }

    public ToshTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ToshTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setText(getText());
    }


    @Override
    public void setBackgroundResource(int resid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            setBackground(resid != 0 ? AppCompatResources.getDrawable(getContext(), resid) : null);
        else
            setBackgroundDrawable(resid != 0 ? AppCompatResources.getDrawable(getContext(), resid) : null);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        //EmojiconHandler.addEmojis(getContext(), builder, BaseBaseUtils.dpToPx(DEFAULT_EMOJI_SIZE), TEXT_START, TEXT_LENGTH);
        super.setText(builder, type);
    }

    /**
     * Sets the reference time for this view. At any moment, the view will render a relative time period relative to the time set here.
     * <p/>
     * This value can also be set init the XML attribute {@code reference_time}
     *
     * @param referenceTime The timestamp (in milliseconds since epoch) that will be the reference point for this view.
     */
    public void setReferenceTime(long referenceTime, String prefix) {

        this.isUsingReferenceTime = true;

        this.mReferenceTime = referenceTime;

        this.mPrefix = prefix;

        /*
         * Note that this method could be called when a row in a ListView is recycled.
         * Hence, we need to first stop any currently running schedules (for example from the recycled view.
         */
        stopTaskForPeriodicallyUpdatingRelativeTime();

        /*
         * Instantiate a new runnable init the new reference time
         */
        mUpdateTimeTask = new UpdateTimeRunnable(this, mReferenceTime);

        /*
         * Start a new schedule.
         */
        startTaskForPeriodicallyUpdatingRelativeTime();

        /*
         * Finally, update the text display.
         */
        updateTextDisplay();
    }

    public void setReferenceTime(long referenceTime){
        setReferenceTime(referenceTime, "");
    }



    private void updateTextDisplay() {
        /*
         * TODO: Validation, Better handling of negative cases
         */
        if (this.mReferenceTime == -1L) return;
        setText(new StringBuilder(mPrefix).append(getRelativeTimeDisplayString()));
    }

    private CharSequence getRelativeTimeDisplayString() {
        long now = System.currentTimeMillis();
        long difference = now - mReferenceTime;
        return (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS) ?
                getResources().getString(R.string.just_now) :
                DateUtils.getRelativeTimeSpanString(
                        mReferenceTime,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isUsingReferenceTime){
            try {
                startTaskForPeriodicallyUpdatingRelativeTime();
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isUsingReferenceTime){
            try {
                stopTaskForPeriodicallyUpdatingRelativeTime();
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            stopTaskForPeriodicallyUpdatingRelativeTime();
        } else {
            startTaskForPeriodicallyUpdatingRelativeTime();
        }
    }

    private void startTaskForPeriodicallyUpdatingRelativeTime() {
        mHandler.post(mUpdateTimeTask);
        isUpdateTaskRunning = true;
    }

    private void stopTaskForPeriodicallyUpdatingRelativeTime() {
        if (isUpdateTaskRunning) {
            if ((mUpdateTimeTask != null) && (mHandler != null)){
                mUpdateTimeTask.detach();
                mHandler.removeCallbacks(mUpdateTimeTask);
            }
            isUpdateTaskRunning = false;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.referenceTime = mReferenceTime;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        mReferenceTime = ss.referenceTime;
        super.onRestoreInstanceState(ss.getSuperState());
    }

    private void linkfy() {
        Matcher hashTag = HASHTAG_PATTERN.matcher(getText());
        Matcher mention = MENTION_PATTERN.matcher(getText());
        Matcher webLink = WEB_PATTERN.matcher(getText());

        SpannableString spannableString = new SpannableString(getText());
        //#hashtags
        while (hashTag.find()) {
            spannableString.setSpan(new NonUnderlinedClickableSpan(hashTag.group(), HASHTAG),
                    hashTag.start(),
                    hashTag.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // @mention
        while (mention.find()) {
            spannableString.setSpan(new NonUnderlinedClickableSpan(mention.group(), MENTION),
                    mention.start(),
                    mention.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //weblink
        while (webLink.find()) {
            spannableString.setSpan(new NonUnderlinedClickableSpan(webLink.group(), WEB),
                    webLink.start(),
                    webLink.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        setText(spannableString);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setLinkEnabledText(String linkEnabledText, @ColorRes int color) {
        if (linkEnabledText == null) {
            linkEnabledText = "";
        }
        if (color == 0) {
            color = android.R.color.black;
        }
        mLinkColor = color;
        setText(linkEnabledText);
        linkfy();
    }

    public static class SavedState extends BaseSavedState {

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private long referenceTime;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            referenceTime = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(referenceTime);
        }
    }

    private static class UpdateTimeRunnable implements Runnable {

        private final WeakReference<ToshTextView> weakRefRttv;
        private long mRefTime;

        UpdateTimeRunnable(ToshTextView rttv, long refTime) {
            this.mRefTime = refTime;
            weakRefRttv = new WeakReference<>(rttv);
        }

        void detach() {
            weakRefRttv.clear();
        }

        @Override
        public void run() {
            ToshTextView rttv = weakRefRttv.get();
            if (rttv == null) return;
            long difference = Math.abs(System.currentTimeMillis() - mRefTime);
            long interval = INITIAL_UPDATE_INTERVAL;
            if (difference > DateUtils.WEEK_IN_MILLIS) {
                interval = DateUtils.WEEK_IN_MILLIS;
            } else if (difference > DateUtils.DAY_IN_MILLIS) {
                interval = DateUtils.DAY_IN_MILLIS;
            } else if (difference > DateUtils.HOUR_IN_MILLIS) {
                interval = DateUtils.HOUR_IN_MILLIS;
            }
            rttv.updateTextDisplay();
            rttv.mHandler.postDelayed(this, interval);

        }
    }

    public class NonUnderlinedClickableSpan extends ClickableSpan {
        int type; // 0-hashtag , 1- mention, 2- url link, 3-read more
        String text;// mention, hashtag or url

        public NonUnderlinedClickableSpan(String text, int type) {
            this.text = text;
            this.type = type;
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            /*switch (type){
                case MENTION:
                    textPaint.setTypeface(Typeface.SANS_SERIF);
                    break;
                case HASHTAG:
                    //textPaint.setTypeface(Typeface.MONOSPACE);
                    break;
                case WEB:

                    break;
            }*/
            //textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setColor(ContextCompat.getColor(getContext(), mLinkColor));
            textPaint.setUnderlineText(false);

        }

        @Override
        public void onClick(View v) {
            switch (type) {
                case MENTION:
                    break;
                case HASHTAG:
                    break;
                case WEB:
                    break;
            }
        }
    }
}

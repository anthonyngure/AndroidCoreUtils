/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Anthony Ngure on 7/1/2016.
 * Email : anthonyngure25@gmail.com.
 */
public class SquaredImageView extends androidx.appcompat.widget.AppCompatImageView {

    public SquaredImageView(Context context) {
        this(context, null);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquaredImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}

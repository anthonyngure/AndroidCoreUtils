/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by abdulmujibaliu on 9/7/17.
 */

public class RoundedView extends View {
    Paint paint = new Paint(Paint.DITHER_FLAG);
    private int uncheckedColor;
    private int checkedColor;
    private int paintColor = uncheckedColor;
    private boolean checked = false;

    public RoundedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundedView, 0, 0);

        uncheckedColor = a.getColor(R.styleable.RoundedView_rvColorUnselected,
                ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        checkedColor = a.getColor(R.styleable.RoundedView_rvColorSelected,
                ContextCompat.getColor(getContext(), android.R.color.black));

        paintColor = uncheckedColor;

        a.recycle();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(paintColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2,
                getWidth() / 2, paint);

    }

    public void setCheckedCircleColor(int color) {
        this.checkedColor = color;
        invalidate();
    }

    public void setUncheckedCircleColor(int color) {
        this.uncheckedColor = color;
        invalidate();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (this.checked) {
            paintColor = checkedColor;
        } else {
            paintColor = uncheckedColor;
        }
        invalidate();
    }


}
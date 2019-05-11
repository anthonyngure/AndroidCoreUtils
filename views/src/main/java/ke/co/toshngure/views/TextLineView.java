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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import ke.co.toshngure.views.utils.Utils;

public class TextLineView extends FrameLayout {

    @ColorRes
    public static final int DEFAULT_COLOR = R.color.colorDivider;

    public TextLineView(Context context) {
        this(context, null);
    }

    public TextLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_text_line_view, this, true);


        TextView textTV = findViewById(R.id.textTV);
        View leftView = findViewById(R.id.leftView);
        View rightView = findViewById(R.id.rightView);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextLineView, defStyle, 0);

        int lineSize = a.getDimensionPixelSize(R.styleable.TextLineView_tlvLineSize, Utils.dpToPx(0.5));
        leftView.getLayoutParams().height = lineSize;
        rightView.getLayoutParams().height = lineSize;

        int lineColor = a.getColor(R.styleable.TextLineView_tlvLineColor, ContextCompat.getColor(getContext(), DEFAULT_COLOR));
        leftView.setBackgroundColor(lineColor);
        rightView.setBackgroundColor(lineColor);

        String text = a.getString(R.styleable.TextLineView_tlvText);
        int textColor = a.getColor(R.styleable.TextLineView_tlvTextColor, ContextCompat.getColor(getContext(), DEFAULT_COLOR));
        textTV.setText(text);
        textTV.setTextColor(textColor);

        a.recycle();
    }

}

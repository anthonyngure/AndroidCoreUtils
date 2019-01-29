/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.views.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;


public class VectorUtils {

    public static void tintDrawable(Context context, Drawable drawable, @ColorRes int color) {
        if (context == null)
            throw new NullPointerException("instance of context can not be null");
        if (drawable == null)
            throw new NullPointerException("instance of drawable can not be null");
        Drawable wrap = DrawableCompat.wrap(drawable);
        wrap = wrap.mutate();
        DrawableCompat.setTint(wrap, ContextCompat.getColor(context, color));
        DrawableCompat.setTintMode(wrap, PorterDuff.Mode.SRC_IN);
    }


    public static void tintDrawable(Drawable drawable, ColorStateList color) {
        if (drawable == null)
            throw new NullPointerException("instance of drawable can not be null");
        Drawable wrap = DrawableCompat.wrap(drawable);
        wrap = wrap.mutate();
        DrawableCompat.setTintList(wrap, color);
        DrawableCompat.setTintMode(wrap, PorterDuff.Mode.SRC_IN);
    }
}

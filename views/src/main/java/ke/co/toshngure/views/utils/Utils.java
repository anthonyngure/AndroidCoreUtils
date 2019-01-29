package ke.co.toshngure.views.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;

/**
 * Created by Anthony Ngure on 23/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class Utils {
    public static int dpToPx(double dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Get the color value for the given color attribute
     */
    @ColorInt
    public static int getColor(Context context, @AttrRes int colorAttrId) {
        int[] attrs = new int[]{colorAttrId /* index 0 */};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int colorFromTheme = ta.getColor(0, 0);
        ta.recycle();
        return colorFromTheme;
    }
}

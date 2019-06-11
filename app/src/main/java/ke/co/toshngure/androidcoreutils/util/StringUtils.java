package ke.co.toshngure.androidcoreutils.util;

import android.text.TextUtils;

import javax.annotation.Nullable;

/**
 * Created by Anthony Ngure on 6/11/2019
 *
 * @author Anthony Ngure
 */
public class StringUtils {

    public static boolean isEmptyString(@Nullable String value){
        return TextUtils.isEmpty(value);
    }
}

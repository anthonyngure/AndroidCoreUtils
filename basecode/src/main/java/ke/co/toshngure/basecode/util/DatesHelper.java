/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * Created by Anthony Ngure on 01/02/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */

public class DatesHelper {

    public static final String US_DATE_PATTERN = "MM/dd/yyyy";
    private static final String TAG = DatesHelper.class.getSimpleName();

    public static CharSequence formatJustTime(Context context, long timestamp) {

        if (timestamp == 0) {
            return "";
        }
        return DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_TIME);
    }

    public static boolean isToday(long when) {
        return DateUtils.isToday(when);
    }

    public static boolean isYesterday(long timestamp) {
        if (timestamp <= 0) {
            return false;
        } else {
            Calendar now = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            now.add(Calendar.DATE, -1);
            return ((now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
                    && (now.get(Calendar.MONTH) == calendar.get(Calendar.MONTH))
                    && (now.get(Calendar.DATE) == calendar.get(Calendar.DATE)));
        }
    }

    public static long formatSqlTimestamp(@Nullable String timestamp) {
        if (timestamp == null) {
            timestamp = new Timestamp(new Date().getTime()).toString();
        }
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }

    public static String formatToSqlTimestamp(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatSqlTimestampForDisplay(String string) {
        String val;
        if (string == null) {
            val = "";
        } else {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(string);
                val = DateFormat.getDateInstance().format(date);
            } catch (ParseException e) {
                val = "Unspecified";
               e.printStackTrace();
            }
        }
        return val;
    }

    public static String formatJustDate(long timestamp) {
        if (timestamp == 0) {
            return "";
        }
        return DateFormat.getDateInstance().format(new Date(timestamp));
    }
}

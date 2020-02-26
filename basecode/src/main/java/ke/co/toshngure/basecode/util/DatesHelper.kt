/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.util

import android.content.Context
import android.text.format.DateUtils
import ke.co.toshngure.basecode.logging.BeeLog
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Anthony Ngure on 01/02/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */

object DatesHelper {

    val US_DATE_PATTERN = "MM/dd/yyyy"
    private val TAG = DatesHelper::class.java.simpleName

    fun formatJustTime(context: Context, timestamp: Long): CharSequence {

        return if (timestamp == 0L) {
            ""
        } else DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_TIME)
    }

    fun isToday(timeInMillis: Long): Boolean {
        return DateUtils.isToday(timeInMillis)
    }

    fun isSameDay(firstTimeInMillis: Long, secondTimeInMillis: Long): Boolean {
        val date1 = Date(firstTimeInMillis)
        val date2 = Date(secondTimeInMillis)
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
        return fmt.format(date1) == fmt.format(date2)
    }

    fun isYesterday(timestamp: Long): Boolean {
        return if (timestamp <= 0) {
            false
        } else {
            val now = Calendar.getInstance()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            now.add(Calendar.DATE, -1)
            (now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    && now.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                    && now.get(Calendar.DATE) == calendar.get(Calendar.DATE))
        }
    }

    fun formatSqlTimestamp(timestamp: String?): Long {
        return timestamp?.let {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(timestamp)
                date.time
            } catch (e: ParseException) {
                BeeLog.e(e)
                Date().time
            }
        } ?: Date().time
    }

    fun formatSqlDate(date: String?): Long {
        return date?.let {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date)
                date.time
            } catch (e: ParseException) {
                BeeLog.e(e)
                Date().time
            }
        } ?: Date().time
    }

    fun formatToSqlTimestamp(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        return simpleDateFormat.format(Date(timestamp))
    }

    fun formatSqlTimestampForDisplay(string: String?): String {
        var value: String
        if (string == null) {
            value = ""
        } else {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(string)
                value = DateFormat.getDateInstance().format(date)
            } catch (e: ParseException) {
                value = "Unspecified"
                e.printStackTrace()
            }

        }
        return value
    }


    fun formatJustDate(timestamp: Long): String {
        return if (timestamp == 0L) {
            ""
        } else DateFormat.getDateInstance().format(Date(timestamp))
    }

    fun toTimeDisplayString(calendar: Calendar): String {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
    }


    fun toDateDisplayString(calendar: Calendar): String {
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
    }

    fun toDateTimeDisplayString(sqlTimestamp: String?): String {
        val timestamp = DatesHelper.formatSqlTimestamp(sqlTimestamp)
        return SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Date(timestamp))
    }

    fun toDateTimeDisplayString(timestamp: Long): String {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Date(timestamp))
    }

    fun formatToSqlDate(timeInMillis: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return simpleDateFormat.format(Date(timeInMillis))
    }

    fun formatToSqlTime(timeInMillis: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        return simpleDateFormat.format(Date(timeInMillis))
    }
}

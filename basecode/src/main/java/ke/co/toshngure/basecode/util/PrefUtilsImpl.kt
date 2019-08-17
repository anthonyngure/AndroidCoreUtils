/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.util

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import ke.co.toshngure.basecode.logging.BeeLog

/**
 * Created by Anthony Ngure on 16/02/2017.
 * Email : anthonyngure25@gmail.com.
 */

open class PrefUtilsImpl(protected val context: Context, private val sharedPreferences: SharedPreferences) {

    protected open fun invalidate() {}

    open fun remove(@StringRes key: Int) {
        sharedPreferences.edit().remove(resolveKey(key)).apply()
        invalidate()
    }

    fun writeString(@StringRes key: Int, value: String?) {
        sharedPreferences.edit().putString(resolveKey(key), value).apply()
        invalidate()
    }

    fun getString(@StringRes key: Int): String? {
        return sharedPreferences.getString(resolveKey(key), "")
    }

    fun getString(@StringRes key: Int, defVal: String): String? {
        return try {
            sharedPreferences.getString(resolveKey(key), defVal)
        } catch (e: Exception){
            BeeLog.e(e)
            defVal
        }
    }

    fun getInt(@StringRes key: Int): Int {
        return try {
            sharedPreferences.getInt(resolveKey(key), 0)
        } catch (ex: Exception){
            BeeLog.e(ex)
            0
        }
    }

    fun writeLong(@StringRes key: Int, value: Long) {
        sharedPreferences.edit().putLong(resolveKey(key), value).apply()
        invalidate()
    }

    fun getLong(@StringRes key: Int): Long {
        return try {
            sharedPreferences.getLong(resolveKey(key), 0)
        } catch (e: Exception) {
            BeeLog.e(e)
            0
        }
    }

    fun getLong(@StringRes key: Int, defVal: Long): Long {
        return try {
            sharedPreferences.getLong(resolveKey(key), defVal.toLong())
        } catch (ex: Exception) {
            BeeLog.e(ex)
            defVal
        }
    }


    fun getBoolean(@StringRes key: Int, defVal: Boolean): Boolean {
        return try {
            sharedPreferences.getBoolean(resolveKey(key), defVal)
        } catch (ex: Exception) {
            BeeLog.e(ex)
            defVal
        }
    }

    fun writeInt(@StringRes key: Int, `val`: Int) {
        sharedPreferences.edit().putInt(resolveKey(key), `val`).apply()
    }

    fun writeBoolean(@StringRes key: Int, value: Boolean) {
        sharedPreferences.edit().putBoolean(resolveKey(key), value).apply()
        invalidate()
    }

    fun getBoolean(@StringRes key: Int): Boolean {
        return try {
            sharedPreferences.getBoolean(resolveKey(key), false)
        } catch (e: Exception) {
            BeeLog.e(e)
            false
        }
    }

    public fun clear() {
        sharedPreferences.edit().clear().apply()
        invalidate()
    }

    private fun resolveKey(@StringRes key: Int): String {
        return "key_" + context.resources.getResourceEntryName(key)
        //return String.valueOf("key_" + key);
        //return getContext().getString(key).trim().replaceAll(" ","");
    }

    companion object {
        private const val TAG = "PrefUtilsImpl"
    }


}

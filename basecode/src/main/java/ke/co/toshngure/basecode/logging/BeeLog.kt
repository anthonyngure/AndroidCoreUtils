/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.logging

import android.util.Log


/**
 * It is used to provide log history in order to show in the bee.
 */
object BeeLog{

    var DEBUG = false
    private var tag: String? = null

    fun init(debug: Boolean, logTag: String) {
        tag = logTag
        DEBUG = debug
    }

    fun d(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.d(tag, "$subTag : $message")
            addToHistory(tag, message.toString())
        }
    }

    fun e(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.e(tag, "$subTag : $message")
            addToHistory(tag, message.toString())
        }
    }

    fun e(subTag: String, e: Exception?) {
        if (DEBUG) {
            if (e != null) {
                Log.e(tag, subTag + " : " + e.localizedMessage)
                addToHistory(tag, e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun e(e: Exception?) {
        if (DEBUG) {
            if (e != null) {
                Log.e(tag," : " + e.localizedMessage)
                addToHistory(tag, e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun w(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.w(tag, "$subTag : $message")
            addToHistory(tag, message.toString())
        }
    }


    fun i(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.i(tag, "$subTag : $message")
            addToHistory(tag, message.toString())
        }
    }

    private fun addToHistory(subTag: String?, message: String?) {
        LogHistoryManager.getInstance().add(LogItem(subTag.toString(), message.toString()))
    }

    fun e(subTag: String, e: Throwable) {
        if (DEBUG) {
            Log.e(tag, subTag + " : " + e.message)
            addToHistory(tag, e.message)
        }
    }
}// no instance

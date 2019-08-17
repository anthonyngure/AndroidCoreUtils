/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.logging

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import ke.co.toshngure.basecode.extensions.executeAsync


/**
 * It is used to provide log history in order to show in the bee.
 */
object BeeLog {

    var DEBUG = false
    private var tag: String? = null
    private var debugSwitchClicks = 0

    fun init(debug: Boolean, logTag: String, context: Context) {
        LogItemsDatabase.init(context)
        tag = logTag
        DEBUG = debug
    }

    fun d(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.d(tag, "$subTag : $message")
            addToHistory(subTag, message.toString())
        }
    }

    fun v(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.v(tag, "$subTag : $message")
            addToHistory(subTag, message.toString())
        }
    }

    fun e(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.e(tag, "$subTag : $message")
            addToHistory(subTag, message.toString())
        }
    }

    fun e(subTag: String, e: Exception?) {
        if (DEBUG) {
            if (e != null) {
                Log.e(tag, subTag + " : " + e.localizedMessage)
                addToHistory(subTag, e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun e(e: Exception?) {
        if (DEBUG) {
            if (e != null) {
                Log.e(tag, " : " + e.localizedMessage)
                addToHistory(tag, e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun w(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.w(tag, "$subTag : $message")
            addToHistory(subTag, message.toString())
        }
    }


    fun i(subTag: String, message: Any?) {
        if (DEBUG) {
            Log.i(tag, "$subTag : $message")
            addToHistory(subTag, message.toString())
        }
    }

    private fun addToHistory(subTag: String?, message: String?) {
        executeAsync {
            val logItem = LogItem(title = subTag.toString(), details = message.toString())
            if (subTag?.contains("LogItem") == false){
                LogItemsDatabase.getInstance().logItems().insert(logItem)
            }
        }
    }

    fun e(subTag: String, e: Throwable) {
        if (DEBUG) {
            Log.e(tag, subTag + " : " + e.message)
            addToHistory(subTag, e.message)
        }
    }

    fun switchDebugWithViewClicks(subTag: String, view: View, clicks: Int = 20) {
        view.setOnClickListener {
            if (debugSwitchClicks == clicks) {
                DEBUG = !DEBUG
                this.debugSwitchClicks = 0
                Toast.makeText(it.context, "Debug $DEBUG", Toast.LENGTH_LONG).show()
            }
            this.debugSwitchClicks++
            i(subTag, "switchDebugWithViewClicks $debugSwitchClicks")
        }
    }

}

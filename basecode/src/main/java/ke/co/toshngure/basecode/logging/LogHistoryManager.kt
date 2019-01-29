/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.logging


import java.util.ArrayList


/**
 * Created by Anthony Ngure on 9/11/2016.
 * Email : anthonyngure25@gmail.com.
 * Company : Laysan Incorporation
 */
class LogHistoryManager private constructor(){
    private val logItems = ArrayList<LogItem>()

    internal fun add(logItem: LogItem) {
        logItems.add(logItem)
    }

    internal fun getLogItems(): List<LogItem> {
        return logItems
    }

    companion object {

        private var instance: LogHistoryManager? = null

        fun getInstance() : LogHistoryManager {
            return instance ?: synchronized(this) {
                instance
                        ?: LogHistoryManager()
            }
        }
    }
}

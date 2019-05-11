/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.logging

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Anthony Ngure on 9/11/2016.
 * Email : anthonyngure25@gmail.com.
 * Company : Laysan Incorporation
 */
@Entity(tableName = "log_items")
data class LogItem constructor(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val details: String) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LogItem>() {
            override fun areItemsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LogItem, newItem: LogItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}

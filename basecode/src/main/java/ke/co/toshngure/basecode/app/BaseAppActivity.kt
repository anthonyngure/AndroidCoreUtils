/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


/**
 * Created by Anthony Ngure on 18/09/2017.
 * Email : anthonyngure25@gmail.com.
 */

open class BaseAppActivity : AppCompatActivity() {

    fun startNewTaskActivity(intent: Intent) {


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }




    companion object {

        private const val TAG = "BaseAppActivity"

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }


}

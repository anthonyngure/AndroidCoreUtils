/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.app

import android.content.Context
import android.os.AsyncTask
import ke.co.toshngure.basecode.R

/**
 * Created by Anthony Ngure on 03/09/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */
abstract class DialogAsyncTask<Params, Progress, Result>(private val context: () -> Context?)
    : AsyncTask<Params, Progress, Result>() {

    private var progressDialog: BaseProgressDialog? = null

    override fun onPreExecute() {
        super.onPreExecute()
        context()?.let {
            try {
                progressDialog = BaseProgressDialog(it)
                progressDialog!!.setMessage(it.getString(R.string.message_waiting))
                        .setCancelable(false)
                        .show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onPostExecute(result: Result) {
        super.onPostExecute(result)
        progressDialog?.let {
            it.dismiss()
            progressDialog = null
        }
    }
}

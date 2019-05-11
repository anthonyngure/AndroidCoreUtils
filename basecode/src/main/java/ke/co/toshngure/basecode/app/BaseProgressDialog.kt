/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.app

import android.content.Context
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import ke.co.toshngure.basecode.R


/**
 * Created by Anthony Ngure on 03/09/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */

class BaseProgressDialog(private val mContext: Context) {

    private val mBottomSheetDialog: BottomSheetDialog?
    private val mView: View
    private val mMessageTV: TextView

    val isShowing: Boolean
        get() = mBottomSheetDialog != null && mBottomSheetDialog.isShowing

    init {
        this.mBottomSheetDialog = BottomSheetDialog(mContext)
        this.mView = LayoutInflater.from(mContext).inflate(R.layout.basecode_view_progress_dialog, null)
        this.mMessageTV = mView.findViewById(R.id.messageTV)
    }


    fun setMessage(message: String): BaseProgressDialog {
        mMessageTV.text = message
        return this
    }

    fun setMessage(@StringRes message: Int): BaseProgressDialog {
        setMessage(mContext.getString(message))
        return this
    }

    fun setCancelable(cancellable: Boolean): BaseProgressDialog {
        mBottomSheetDialog!!.setCancelable(cancellable)
        return this
    }

    fun show() {
        mBottomSheetDialog!!.setContentView(mView)
        mBottomSheetDialog.show()
    }

    fun dismiss() {
        mBottomSheetDialog?.dismiss()
    }
}

/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

@file:Suppress("DEPRECATION")

package ke.co.toshngure.basecode.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.Menu
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import ke.co.toshngure.basecode.logging.BeeLog
import java.util.*


/**
 * Created by Anthony Ngure on 17/02/2017.
 * Email : anthonyngure25@gmail.com.
 */

object BaseUtils {

    private const val PRICE_FORMAT = "%,.2f"
    private const val CURRENCY_CODE = "KSH "
    private var screenHeight = 0
    private var screenWidth = 0

    fun isValidFullName(name: String?): Boolean {
        return name.toString().split("\\s".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray().size >= 2 && !TextUtils.isDigitsOnly(name)
    }

    fun isValidPhone(phoneNo: String?): Boolean {
        return phoneNo?.let {
            !TextUtils.isEmpty(it) && Patterns.PHONE.matcher(it).matches() && it.length == 10
        } ?: false
    }

    fun tintProgressBar(progressBar: ProgressBar, @ColorInt color: Int) {
        progressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun tintImageView(imageView: ImageView, @ColorInt color: Int) {
        imageView.setColorFilter(color)
    }


    fun makeCall(context: Context, phone: String?) {
        phone?.let {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            try {
                if (callIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(Intent.createChooser(callIntent, "Call with..."))
                } else {
                    Toast.makeText(context, "Unable to find a calling application.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to make a call.", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(context, "Unable to make a call.", Toast.LENGTH_SHORT).show()

    }

    fun uuidToLong(id: String?): Long {
        return (31 * 2 + (id?.hashCode() ?: 0)).toLong()
    }

    fun formatPrice(price: Double): String {
        return CURRENCY_CODE + String.format(Locale.ENGLISH, PRICE_FORMAT, price)
    }


    fun formatPrice(price: String?): String {
        val amount = try {
            price?.toDouble() ?: 0.0
        } catch (e : Exception){
            BeeLog.e(e)
            0.0
        }
        return formatPrice(amount)
    }

    fun openUrl(context: Context, url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }


    fun sendSms(context: Context, phone: String?, msg: String = "") {
        phone?.let {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                type = "text/plain"
                data = Uri.parse("smsto:$phone")
                putExtra("sms_body", msg)
                putExtra(Intent.EXTRA_TEXT, msg)
            }
            try {
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(Intent.createChooser(intent, "Message with..."))
                } else {
                    Toast.makeText(context, "Unable to find a messaging application.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to find a messaging application.", Toast.LENGTH_SHORT).show()
            }
        }
                ?: Toast.makeText(context, "Unable to find a messaging application.", Toast.LENGTH_SHORT).show()
    }

    fun shareText(context: Context, text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        try {
            if (sendIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(sendIntent, "Share with..."))
            } else {
                Toast.makeText(context, "Unable to find a messaging application.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to find a messaging application.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(context: Context, emailAddress: String, subject: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailAddress, null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }


    fun cacheInput(editText: EditText, @StringRes key: Int, prefUtils: PrefUtilsImpl) {
        // Get cached text
        val currentInput = prefUtils.getString(key)
        // Set current text to the cached value
        editText.setText(currentInput)
        // Set cursor to the end of the text
        editText.setSelection(editText.text?.length ?: 0)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                prefUtils.writeString(key, charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    fun getScreenHeight(context: Context): Int {
        if (screenHeight == 0) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            screenHeight = size.y
        }

        return screenHeight
    }

    fun getScreenWidth(context: Context): Int {
        if (screenWidth == 0) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            screenWidth = size.x
        }

        return screenWidth
    }


    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }


    /**
     * Get the color value for the given color attribute
     */
    @ColorInt
    fun getColor(context: Context, @AttrRes colorAttrId: Int): Int {
        val attrs = intArrayOf(colorAttrId /* index 0 */)
        val ta = context.obtainStyledAttributes(attrs)
        val colorFromTheme = ta.getColor(0, 0)
        ta.recycle()
        return colorFromTheme
    }

    fun tintMenu(menu: Menu?, @ColorInt color: Int) {
        if (menu != null && menu.size() > 0) {
            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                val icon = item.icon
                if (icon != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        icon.setTint(color)
                    } else {
                        icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    }
                }
            }
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    fun appIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
        return isInBackground
    }


}

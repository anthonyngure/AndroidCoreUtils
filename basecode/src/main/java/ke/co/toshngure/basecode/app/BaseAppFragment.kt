/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.app

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.extensions.hide
import ke.co.toshngure.basecode.extensions.show
import ke.co.toshngure.basecode.logging.BeeLog
import kotlinx.android.synthetic.main.fragment_base.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Anthony Ngure on 11/06/2017.
 * Email : anthonyngure25@gmail.com.
 */

abstract class BaseAppFragment<D> : Fragment() {

    private var mPermissionsRationale =
        "Required permissions have been denied. Please allow requested permissions to proceed\n" +
                "\n Go to [Setting] > [Permission]"

    private var mActiveRetrofitCallback: CancelableCallback? = null

    private inner class RequiredPermissionsListener(private val navigationAction: () -> Unit) : PermissionListener {


        override fun onPermissionGranted() {
            navigationAction()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingLayout.hide()
        onSetUpCollapsibleView(collapsibleViewContainer)
        initSwipeToRefresh()
        onSetUpTopView(topViewContainer)
        onSetUpContentView(contentViewContainer)
        onSetUpBottomView(bottomViewContainer)
    }


    private fun initSwipeToRefresh() {

        swipeRefreshLayout.isEnabled = false

        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )

        swipeRefreshLayout.setOnRefreshListener {
            if (!swipeRefreshLayout.isRefreshing && getApiCall() != null) {
                swipeRefreshLayout.isRefreshing = true
                makeRequest()
            } else {
                swipeRefreshLayout.isRefreshing = false
            }
        }
        onSetUpSwipeRefreshLayout(swipeRefreshLayout)
    }


    protected open fun onSetUpSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {}

    protected open fun onSetUpCollapsibleView(container: FrameLayout) {}

    protected open fun onSetUpTopView(container: FrameLayout) {}

    protected open fun onSetUpContentView(container: FrameLayout) {}

    protected open fun onSetUpBottomView(container: FrameLayout) {}


    fun toast(message: Any) {

        try {
            Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun toastDebug(msg: Any) {
        if (BeeLog.DEBUG) {
            toast(msg)
        }
    }

    fun toast(@StringRes string: Int) {
        toast(getString(string))
    }

    protected fun makeRequest() {
        getApiCall()?.let { call ->
            loadingLayout.show()
            val callback = CancelableCallback()
            call.enqueue(callback)
            mActiveRetrofitCallback = callback
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mActiveRetrofitCallback?.cancel()
        mActiveRetrofitCallback = null
    }

    private inner class CancelableCallback : Callback<D> {

        private var canceled = false

        fun cancel() {
            canceled = true
        }

        override fun onFailure(call: Call<D>, t: Throwable) {
            BeeLog.e(TAG, "onFailure")
            BeeLog.e(TAG, t)
            if (!canceled) {
                mActiveRetrofitCallback = null
                if (BeeLog.DEBUG) {
                    showNetworkErrorDialog(t.message)
                } else {
                    showNetworkErrorDialog(getString(R.string.message_connection_error))
                }
            }
        }

        override fun onResponse(call: Call<D>, response: Response<D>) {
            BeeLog.e(TAG, "onResponse, $response")
            loadingLayout.hide()
            if (!canceled) {
                mActiveRetrofitCallback = null
                if (response.isSuccessful) {
                    val body: D? = response.body()
                    when {
                        body != null -> {
                            DataHandlerTask(
                                this@BaseAppFragment::processDataInBackground,
                                this@BaseAppFragment::onDataReady
                            ).execute(body)
                        }
                        else -> {
                            showNetworkErrorDialog("Invalid body response")
                        }
                    }
                } else {
                    showNetworkErrorDialog(getErrorMessage(response))
                }
            }
        }
    }

    private fun getErrorMessage(response: Response<D>): String {
        val errorBody = response.errorBody()
        return errorBody?.let {
             getErrorMessageFromResponseBody(response.code(), it)
        } ?: response.message()
    }

    protected open fun getErrorMessageFromResponseBody(statusCode: Int, data: ResponseBody): String {
        return getString(R.string.message_connection_error)
    }

    protected open fun processDataInBackground(data: D): D {
        return data
    }

    protected open fun onDataReady(data: D) {

    }

    private class DataHandlerTask<D>(
        private val processData: (data: D) -> D,
        private val onFinish: (data: D) -> Unit
    ) : AsyncTask<D, Void, D>() {
        override fun doInBackground(vararg params: D): D {
            return processData(params[0])
        }

        override fun onPostExecute(result: D) {
            super.onPostExecute(result)
            onFinish(result)
        }

    }


    protected open fun getApiCall(): Call<D>? {
        return null
    }

    private fun showNetworkErrorDialog(message: String?) {
        activity?.let {
            AlertDialog.Builder(it)
                .setMessage(message ?: getString(R.string.message_connection_error))
                .setPositiveButton(R.string.retry) { _, _ -> makeRequest() }
                .setNegativeButton(R.string.cancel) { _, _ ->

                }.show()
        }
    }

    protected fun hideKeyboardFrom(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun showErrorSnack(msg: String) {
        view?.let {
            Snackbar.make(it, msg, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok) {}.show()
        }
    }

    protected fun showErrorSnack(@StringRes msg: Int) {
        showErrorSnack(getString(msg))
    }

    protected fun navigateWithPermissionsCheck(directions: NavDirections, vararg permissions: String) {
        navigateWithPermissionsCheck(*permissions, navigationAction = {
            view?.findNavController()?.navigate(directions, defaultNavOptions())
        })
    }

    protected fun navigateWithPermissionsCheck(@IdRes resId: Int, args: Bundle? = null, vararg permissions: String) {
        navigateWithPermissionsCheck(*permissions, navigationAction = {
            view?.findNavController()?.navigate(resId, args, defaultNavOptions())
        })
    }

    protected fun startActivityWithPermissionsCheck(intent: Intent, vararg permissions: String) {
        navigateWithPermissionsCheck(*permissions, navigationAction = {
            startActivity(intent)
        })
    }

    protected fun startActivityWithPermissionsCheck(intent: Intent, requestCode: Int, vararg permissions: String) {
        navigateWithPermissionsCheck(*permissions, navigationAction = {
            startActivityForResult(intent, requestCode)
        })
    }

    private fun defaultNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    }

    private fun navigateWithPermissionsCheck(vararg permissions: String, navigationAction: () -> Unit) {
        if (!permissions.isNullOrEmpty()) {
            TedPermission.with(context)
                .setPermissionListener(RequiredPermissionsListener(navigationAction))
                .setDeniedMessage(mPermissionsRationale)
                .setPermissions(*permissions)
                .check()
        } else {
            navigationAction()
        }
    }

    companion object {
        private const val TAG = "BaseAppFragment"
    }


}

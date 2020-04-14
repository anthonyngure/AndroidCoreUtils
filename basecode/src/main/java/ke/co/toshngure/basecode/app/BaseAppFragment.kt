/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import ke.co.toshngure.basecode.R
import ke.co.toshngure.basecode.logging.BeeLog
import ke.co.toshngure.basecode.net.NetworkUtil
import ke.co.toshngure.basecode.util.BaseUtils
import ke.co.toshngure.extensions.hide
import ke.co.toshngure.extensions.show
import ke.co.toshngure.extensions.showIf
import kotlinx.android.synthetic.main.basecode_fragment_base_app.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Anthony Ngure on 11/06/2017.
 * Email : anthonyngure25@gmail.com.
 */

abstract class BaseAppFragment<FetchedNetworkModel> : Fragment(),
    SwipeRefreshLayout.OnRefreshListener {


    internal lateinit var mLoadingConfig: LoadingConfig

    private var mPermissionsRationale =
        "Required permissions have been denied. Please allow requested permissions to proceed\n" +
                "\n Go to [Setting] > [Permission]"

    private var mActiveRetrofitCallback: CancelableCallback? = null

    private inner class RequiredPermissionsListener(private val navigationAction: () -> Unit) :
        PermissionListener {


        override fun onPermissionGranted() {
            navigationAction.invoke()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.basecode_fragment_base_app, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoadingConfig = getLoadingConfig()

        noDataMessageTV.setText(mLoadingConfig.noDataMessage)
        noDataIV.setImageResource(mLoadingConfig.noDataIcon)

        errorIV.setImageResource(mLoadingConfig.errorIcon)

        statusTV.showIf(BeeLog.DEBUG && BeeLog.showStatusTextView)

        loadingLayout.hide()
        if (mLoadingConfig.withLoadingLayoutAtTop) {
            loadingLayout.gravity = Gravity.TOP or Gravity.CENTER
            (loadingProgressBar.layoutParams as LinearLayout.LayoutParams).topMargin =
                BaseUtils.dpToPx(56)
        } else {
            loadingLayout.gravity = Gravity.CENTER
        }

        noDataLayout.hide()
        if (mLoadingConfig.withNoDataLayoutAtTop) {
            noDataLayout.gravity = Gravity.TOP or Gravity.CENTER
            (noDataIV.layoutParams as LinearLayout.LayoutParams).topMargin = BaseUtils.dpToPx(56)
        } else {
            noDataLayout.gravity = Gravity.CENTER
        }

        errorLayout.hide()
        if (mLoadingConfig.withErrorLayoutAtTop) {
            errorLayout.gravity = Gravity.TOP or Gravity.CENTER
            (errorIV.layoutParams as LinearLayout.LayoutParams).topMargin = BaseUtils.dpToPx(56)
        } else {
            errorLayout.gravity = Gravity.CENTER
        }

        onSetUpSwipeRefreshLayout(swipeRefreshLayout)

        onSetUpContentView(contentViewContainer)

        onSetUpBottomViewContainer(bottomViewContainer)

        onSetUpTopViewContainer(topViewContainer)

        onSetUpCollapsibleViewContainer(
            appBarLayout,
            collapsingToolbarLayout,
            collapsibleViewContainer
        )
    }

    protected open fun getLoadingConfig(): LoadingConfig {
        return NetworkUtil.getCallback().getDefaultLoadingConfig()
    }

    protected open fun onSetUpSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {

        swipeRefreshLayout.isEnabled = mLoadingConfig.refreshEnabled

        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )

        swipeRefreshLayout.setOnRefreshListener {
            if (!swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = true
                makeRequest()
            }
        }

        swipeRefreshLayout.setOnRefreshListener(this)


    }

    protected open fun onSetUpTopViewContainer(container: FrameLayout) {}


    protected open fun onSetUpCollapsibleViewContainer(
        appBarLayout: AppBarLayout,
        collapsingToolbarLayout: CollapsingToolbarLayout,
        container: FrameLayout
    ) {
    }

    protected open fun onSetUpContentView(container: FrameLayout) {}

    protected open fun onSetUpBottomViewContainer(container: FrameLayout) {}

    fun toast(message: Any) {

        try {
            Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected fun toastDebug(msg: Any) {
        if (BeeLog.DEBUG) {
            toast(msg)
        }
    }

    protected fun toast(@StringRes string: Int) {
        toast(getString(string))
    }

    protected fun makeRequest() {
        getApiCall()?.let { call ->
            onShowLoading(loadingLayout, contentViewContainer)
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

    private inner class CancelableCallback : Callback<FetchedNetworkModel> {

        private var canceled = false

        fun cancel() {
            canceled = true
        }

        override fun onFailure(call: Call<FetchedNetworkModel>, t: Throwable) {
            BeeLog.e(TAG, "onFailure, throwable -> $t")
            BeeLog.e(TAG, "onFailure, Call isCanceled -> ${call.isCanceled}")
            BeeLog.e(TAG, "onFailure, Call isExecuted -> ${call.isExecuted}")
            BeeLog.e(TAG, "onFailure, Call -> $call")
            onHideLoading(loadingLayout, contentViewContainer)
            if (!canceled) {
                mActiveRetrofitCallback = null
                if (BeeLog.DEBUG) {
                    showNetworkErrorDialog(t.localizedMessage)
                } else {
                    showNetworkErrorDialog(getString(R.string.message_connection_error))
                }
            }
        }

        override fun onResponse(
            call: Call<FetchedNetworkModel>,
            response: Response<FetchedNetworkModel>
        ) {
            BeeLog.i(TAG, "onResponse, $response")
            onHideLoading(loadingLayout, contentViewContainer)
            if (!canceled) {
                mActiveRetrofitCallback = null
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    DataHandlerTask(
                        this@BaseAppFragment::processDataInBackground,
                        this@BaseAppFragment::onDataReady
                    ).execute(body)
                } else if (response.code() == 404) {
                    errorLayout.show()
                    errorMessageTV.setText(mLoadingConfig.noDataMessage)
                } else {

                    val errorResponseBody = response.errorBody()?.string() ?: response.message()

                    val errorMessage = NetworkUtil.getCallback()
                        .getErrorMessageFromResponseBody(response.code(), errorResponseBody)

                    // Handle the error
                    // If the error has been handled in a child class
                    val handledError = onRequestError(response.code(), errorResponseBody)
                    if (!handledError) {
                        showNetworkErrorDialog(errorMessage)
                    }

                }
            }
        }
    }


    protected open fun onShowLoading(
        loadingLayout: LinearLayout?,
        contentViewContainer: FrameLayout?
    ) {
        hideKeyboard()
        loadingLayout?.showIf(mLoadingConfig.showLoading)
        noDataLayout?.hide()
        errorLayout?.hide()
        loadingMessageTV?.setText(mLoadingConfig.loadingMessage)
    }

    override fun onRefresh() {

    }

    protected fun getRefreshLayout(): SwipeRefreshLayout? {
        return swipeRefreshLayout
    }

    protected open fun onHideLoading(
        loadingLayout: LinearLayout?, contentViewContainer: FrameLayout?
    ) {
        loadingLayout?.hide()
        noDataLayout?.hide()
        errorLayout?.hide()
        if (swipeRefreshLayout?.isRefreshing == true) {
            swipeRefreshLayout?.isRefreshing = false
        }
    }

    protected open fun processDataInBackground(data: FetchedNetworkModel): FetchedNetworkModel {
        return data
    }

    /**
     * If you override this method and handle the error on your own,
     * return true so that a dialog is not shown
     * Not called when status code is zero
     * Invoked only when there is a response from the server
     */
    protected open fun onRequestError(statusCode: Int, errorResponseBody: String): Boolean {

        return false
    }

    protected open fun onDataReady(data: FetchedNetworkModel) {}

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


    protected open fun getApiCall(): Call<FetchedNetworkModel>? {
        return null
    }

    private fun showNetworkErrorDialog(message: String?) {
        if (mLoadingConfig.showErrorDialog) {
            activity?.let {
                MaterialAlertDialogBuilder(it)
                    .setCancelable(false)
                    .setMessage(message ?: getString(R.string.message_connection_error))
                    .setPositiveButton(R.string.retry) { _, _ -> makeRequest() }
                    .setNegativeButton(R.string.close) { _, _ -> }
                    .show()
            }
        }

    }

    protected fun hideKeyboardFrom(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun showErrorSnack(msg: String) {
        view?.let {
            Snackbar.make(it, msg, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok) {}
                .show()
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun showSnack(msg: String) {
        view?.let {
            Snackbar.make(it, msg, Snackbar.LENGTH_LONG).setAction(android.R.string.ok) {}.show()
        }
    }

    fun showSnack(@StringRes msg: Int) {
        showSnack(getString(msg))
    }

    fun showErrorSnack(@StringRes msg: Int) {
        showErrorSnack(getString(msg))
    }

    fun navigateWithPermissionsCheck(
        directions: NavDirections, permissions: Array<String> = arrayOf(),
        popUpToDestinationId: Int = 0, popUpToInclusive: Boolean = false
    ) {

        handleActionWithPermissions(*permissions, action = {
            view?.findNavController()
                ?.navigate(
                    directions,
                    BaseUtils.defaultNavOptions(popUpToDestinationId, popUpToInclusive)
                )
        })

    }

    fun navigateWithPermissionsCheck(
        @IdRes resId: Int, args: Bundle? = null, permissions: Array<String> = arrayOf(),
        popUpToDestinationId: Int = 0, popUpToInclusive: Boolean = false
    ) {
        handleActionWithPermissions(*permissions, action = {
            view?.findNavController()
                ?.navigate(
                    resId,
                    args,
                    BaseUtils.defaultNavOptions(popUpToDestinationId, popUpToInclusive)
                )
        })
    }

    fun startActivityWithPermissionsCheck(intent: Intent, vararg permissions: String) {
        handleActionWithPermissions(*permissions, action = {
            startActivity(intent)
        })
    }

    fun startActivityWithPermissionsCheck(
        intent: Intent,
        requestCode: Int,
        vararg permissions: String
    ) {
        handleActionWithPermissions(*permissions, action = {
            startActivityForResult(intent, requestCode)
        })
    }


    protected open fun handleActionWithPermissions(vararg permissions: String, action: () -> Unit) {
        if (!permissions.isNullOrEmpty()) {
            TedPermission.with(context)
                .setPermissionListener(RequiredPermissionsListener(action))
                .setDeniedMessage(mPermissionsRationale)
                .setPermissions(*permissions)
                .check()
        } else {
            action.invoke()
        }
    }

    protected fun setTitle(@StringRes title: Int) {
        setTitle(getString(title))
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun setTitle(title: String?) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    companion object {
        const val TAG = "BaseAppFragment"
    }

    open fun hideKeyboard() {
        activity?.let {
            val imm = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = it.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(it)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}

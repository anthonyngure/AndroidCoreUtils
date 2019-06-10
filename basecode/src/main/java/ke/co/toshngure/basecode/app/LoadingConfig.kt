package ke.co.toshngure.basecode.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ke.co.toshngure.basecode.R

data class LoadingConfig(
        val refreshEnabled: Boolean = false,
        val showNoDataLayout: Boolean = true,
        val showLoading: Boolean = true,
        val showErrorDialog: Boolean = true,
        val withLoadingLayoutAtTop: Boolean = false,
        val withNoDataLayoutAtTop: Boolean = false,
        @StringRes val loadingMessage: Int = R.string.message_waiting,
        @StringRes val noDataMessage: Int = R.string.message_empty_data,
        @DrawableRes val noDataIcon: Int = R.drawable.ic_cloud_queue_black_24dp)
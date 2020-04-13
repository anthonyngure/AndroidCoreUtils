package ke.co.toshngure.basecode.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class LoadingConfig(
    val refreshEnabled: Boolean,
    val showNoDataLayout: Boolean,
    val showLoading: Boolean,
    val showErrorDialog: Boolean,
    val withLoadingLayoutAtTop: Boolean,
    val withNoDataLayoutAtTop: Boolean,
    val withErrorLayoutAtTop: Boolean,
    @StringRes val loadingMessage: Int,
    @StringRes val noDataMessage: Int,
    @DrawableRes val noDataIcon: Int,
    @DrawableRes val errorIcon: Int
)
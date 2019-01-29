package ke.co.toshngure.basecode.dataloading

import androidx.annotation.LayoutRes

data class Config(@LayoutRes val layoutRes: Int,
                               val withDivider: Boolean = true,
                               val autoLoad: Boolean = true,
                               val refreshEnabled: Boolean = true,
                               val showDialog: Boolean = false)
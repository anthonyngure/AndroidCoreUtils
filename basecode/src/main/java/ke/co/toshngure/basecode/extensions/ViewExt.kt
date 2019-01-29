package ke.co.toshngure.basecode.extensions

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import ke.co.toshngure.basecode.R


fun View.hide(){
  this.visibility = View.GONE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.navigate(directions: NavDirections){
    this.findNavController().navigate(directions, defaultNavOptions())
}
fun View.navigate(@IdRes resId: Int, args: Bundle? = null){
    this.findNavController().navigate(resId, args, defaultNavOptions())
}
private fun defaultNavOptions() : NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()
}
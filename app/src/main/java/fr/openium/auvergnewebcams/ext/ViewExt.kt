package fr.openium.auvergnewebcams.ext

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

/**
 * Created by t.coulange on 21/04/16.
 */
fun View.animateCompat(): ViewPropertyAnimatorCompat {
    return ViewCompat.animate(this)
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.goneWithAlphaZero() {
    this.alpha = 0f
    this.gone()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hideWithAnimation() {
    animateCompat().alpha(0f).withEndAction {
        gone()
    }
}

fun View.showWithAnimation() {
    animateCompat().alpha(1f).withStartAction {
        show()
    }
}

fun View.dip(value: Int, type: Int = TypedValue.COMPLEX_UNIT_DIP): Float {
    return dip(value.toFloat(), type)
}

fun View.dip(value: Float, type: Int = TypedValue.COMPLEX_UNIT_DIP): Float {
    return dip(context, value, type)
}

fun Fragment.dip(value: Int, type: Int = TypedValue.COMPLEX_UNIT_DIP): Int {
    return dip(value.toFloat(), type)
}

fun Fragment.dip(value: Float, type: Int = TypedValue.COMPLEX_UNIT_DIP): Int {
    return dip(context!!, value, type).toInt()
}

fun dip(context: Context, value: Float, type: Int = TypedValue.COMPLEX_UNIT_DIP): Float {
    val metrics = context.getResources().getDisplayMetrics()
    val resultPix = TypedValue.applyDimension(type, value, metrics)
    return resultPix
}

fun Button.performClickIfEnabled() {
    if (isEnabled) {
        performClick()
    }
}

fun TextInputLayout.setError(@StringRes id: Int) {
    isErrorEnabled = true
    error = context.getString(id)
}


fun TextInputLayout.clearError() {
    isErrorEnabled = false
    error = ""
}

fun TextView.textToLowerCase() {
    text = text.toString().toLowerCase()
}

fun TextView.textTrimmed(): String {
    return text.toString().trim()
}

fun NavigationView.selectItem(@IdRes menuId: Int) {
    getMenu().performIdentifierAction(menuId, 0);
}

fun EditText.text(): String {
    return text?.toString() ?: ""
}

fun ViewPager.addOnPageSelectedListener(pageListener: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            pageListener.invoke(position)
        }

    })
}
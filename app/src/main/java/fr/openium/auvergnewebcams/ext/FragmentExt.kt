package fr.openium.auvergnewebcams.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

/**
 * Created by t.coulange on 22/04/16.
 */

fun Fragment.isLollipopOrMore(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

val Fragment.appCompatActivity: AppCompatActivity?
    get() {
        if (activity != null) {
            return activity as AppCompatActivity
        }
        return null
    }

inline fun <reified T : Activity> Fragment.startActivity(bundle: Bundle? = null) {
    startActivity(Intent(context, T::class.java).apply {
        if (bundle != null) {
            putExtras(bundle)
        }
    })
}

inline fun <reified T : Activity> Fragment.startActivityForResult(bundle: Bundle? = null, requestCode: Int) {
    startActivityForResult(Intent(context, T::class.java).apply {
        if (bundle != null) {
            putExtras(bundle)
        }
    }, requestCode)
}

fun Fragment.snackbar(str: String, @BaseTransientBottomBar.Duration length: Int): Snackbar? {
    var snack: Snackbar? = null
    if (view != null) {
        snack = Snackbar.make(view!!, str, length)
        snack.show()
    }
    return snack
}

val Fragment.applicationContext: Context
    get() {
        return activity!!.applicationContext
    }

fun Fragment.snackbar(@StringRes id: Int, @BaseTransientBottomBar.Duration length: Int): Snackbar? {
    var snack: Snackbar? = null
    if (view != null) {
        snack = Snackbar.make(view!!, id, length)
        snack.show()
    }
    return snack
}

fun Fragment.toast(@StringRes id: Int, @BaseTransientBottomBar.Duration length: Int): Toast? {
    var toast: Toast? = null
    if (view != null) {
        toast = Toast.makeText(context, id, length)
        toast.show()
    }
    return toast
}

fun Fragment.snackbar(@StringRes textId: Int, @BaseTransientBottomBar.Duration length: Int, @StringRes actionId: Int, body: (View) -> Unit): Snackbar? {
    var snack: Snackbar? = null
    if (view != null) {
        snack = Snackbar.make(view!!, textId, length).setAction(actionId, body)
        snack.show()
    }
    return snack
}

fun Fragment.setTitle(title: String) {
    (activity as AppCompatActivity?)?.supportActionBar?.title = title
}

fun Fragment.setTitle(@StringRes title: Int) {
    (activity as AppCompatActivity?)?.supportActionBar?.setTitle(title)
}

fun Fragment.popBackStackActivity() {
    if (!(activity?.isFinishing() ?: true) && activity?.supportFragmentManager != null) {
        activity!!.supportFragmentManager.popBackStack()
    }
}

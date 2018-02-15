package fr.openium.auvergnewebcams.ext

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle

/**
 * Created by t.coulange on 25/04/16.
 */
inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) {
    startActivity(Intent(applicationContext, T::class.java).apply {
        if (bundle != null) {
            putExtras(bundle)
        }
    })
}

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int, bundle: Bundle? = null) {
    startActivityForResult(Intent(applicationContext, T::class.java).apply {
        if (bundle != null) {
            putExtras(bundle)
        }
    }, requestCode)
}


inline fun <reified T : Service> Activity.startService(bundle: Bundle? = null) {
    startService(Intent(applicationContext, T::class.java).apply {
        if (bundle != null) {
            putExtras(bundle)
        }
    })
}


fun Activity.isLollipopOrMore(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
}
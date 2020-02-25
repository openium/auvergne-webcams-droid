package fr.openium.auvergnewebcams.ext

import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

@Suppress("DEPRECATION")
val Context.hasNetwork: Boolean
    get() {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

fun Context.getFontCompat(@FontRes resId: Int): Typeface? = ResourcesCompat.getFont(this, resId)
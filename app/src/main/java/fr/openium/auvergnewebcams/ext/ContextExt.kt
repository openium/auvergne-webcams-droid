package fr.openium.auvergnewebcams.ext

import android.content.Context
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
val Context.hasNetwork: Boolean
    get() {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }
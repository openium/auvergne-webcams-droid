package fr.openium.auvergnewebcams.ext

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.core.content.pm.PackageInfoCompat
import fr.openium.auvergnewebcams.R

@Suppress("DEPRECATION")
val Context.hasNetwork: Boolean
    get() {
        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

fun Context.isLocationEnabled(): Boolean {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Context.navigateToLocationSettings() {
    if (!isLocationEnabled()) {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}

fun Context.getAppVersion(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        getString(
            R.string.settings_version_format,
            packageInfo.versionName,
            PackageInfoCompat.getLongVersionCode(packageInfo).toString()
        )
    } catch (e: Exception) {
        ""
    }
}


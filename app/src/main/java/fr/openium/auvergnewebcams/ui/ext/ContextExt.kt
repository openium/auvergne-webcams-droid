package fr.openium.auvergnewebcams.ui.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent
import fr.openium.auvergnewebcams.ui.utils.CustomTabsHelper

fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
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


fun Context.navigateToLink(uri: Uri) {
    val packageName = CustomTabsHelper.getPackageNameToUse(this)

    // If we cant find a package name, it means theres no browser that supports
    // Chrome Custom Tabs installed. So, we fallback to the default browser
    packageName?.let {
        CustomTabsIntent.Builder().build().apply {
            intent.setPackage(packageName)
            launchUrl(this@navigateToLink, uri)
        }
    } ?: startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, uri), null))
}

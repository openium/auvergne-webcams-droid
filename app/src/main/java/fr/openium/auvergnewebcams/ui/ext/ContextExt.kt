package fr.openium.auvergnewebcams.ui.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import fr.openium.auvergnewebcams.ui.utils.CustomTabsHelper


fun Context.navigateToLink(uri: Uri) {
    val packageName = CustomTabsHelper.getPackageNameToUse(this)

    packageName?.let {
        CustomTabsIntent.Builder().build().apply {
            intent.setPackage(packageName)
            launchUrl(this@navigateToLink, uri)
        }
    } ?: startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, uri), null))
}

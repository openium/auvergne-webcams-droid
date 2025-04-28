package fr.openium.auvergnewebcams.ui.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.core.net.toUri
import timber.log.Timber

/**
 * Helper class for Custom Tabs.
 * Cf. https://github.com/GoogleChrome/android-browser-helper/tree/main/demos/custom-tabs-example-app/src/main/java/org/chromium/customtabsdemos
 */
object CustomTabsHelper {

    private const val TAG = "CustomTabsHelper"
    private const val ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService"

    private var sPackageNameToUse: String? = null

    /**
     * Goes through all apps that handle VIEW intents and have a warmup service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     *
     * This is **not** threadsafe.
     *
     * @param context [Context] to use for accessing [PackageManager].
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    fun getPackageNameToUse(context: Context): String? {
        if (sPackageNameToUse != null) return sPackageNameToUse

        val pm = context.packageManager

        // Get default VIEW intent handler.
        val activityIntent = Intent(Intent.ACTION_VIEW, ("http://www.example.com").toUri())
        val defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0)
        var defaultViewHandlerPackageName: String? = null

        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName
        }

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs: MutableList<String?> = ArrayList()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls. Prefer the default browser if it supports Custom Tabs.
        sPackageNameToUse =
            if (packagesSupportingCustomTabs.isEmpty()) {
                null
            } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(context, activityIntent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)
            ) {
                defaultViewHandlerPackageName
            } else {
                // Otherwise, pick the next favorite Custom Tabs provider.
                packagesSupportingCustomTabs[0]
            }
        return sPackageNameToUse
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     * @param intent The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private fun hasSpecializedHandlerIntents(context: Context, intent: Intent): Boolean {
        try {
            val pm = context.packageManager
            val handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)

            if (handlers.size == 0) {
                return false
            }

            for (resolveInfo in handlers) {
                val filter = resolveInfo.filter ?: continue
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue
                if (resolveInfo.activityInfo == null) continue
                return true
            }
        } catch (e: RuntimeException) {
            Timber.e(TAG, "Runtime exception while getting specialized handlers")
        }
        return false
    }
}
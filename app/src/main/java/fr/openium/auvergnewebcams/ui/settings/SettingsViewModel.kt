package fr.openium.auvergnewebcams.ui.settings

import android.app.Application
import androidx.core.content.pm.PackageInfoCompat
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractViewModel
import org.koin.core.component.KoinComponent

class SettingsViewModel(private val app: Application) : AbstractViewModel(app), KoinComponent {

    fun getAppVersion(): String {
        return try {
            val packageInfo = app.packageManager.getPackageInfo(app.packageName, 0)
            app.getString(
                R.string.settings_version_format,
                packageInfo.versionName,
                PackageInfoCompat.getLongVersionCode(packageInfo).toString()
            )
        } catch (e: Exception) {
            ""
        }
    }


}
package fr.openium.auvergnewebcams.ext

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.pm.PackageInfoCompat
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Webcam


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

fun Context.launchSignalWebcamNotWorking(webcam: Webcam, onError: () -> Unit) {
    val subject = getString(
        R.string.detail_signal_problem_subject, webcam.title ?: ""
    )
    val body = getString(
        R.string.detail_signal_problem_body_format, webcam.title ?: "", webcam.uid.toString()
    )
    val mailUri = Uri.parse(
        "mailto:${getString(R.string.detail_signal_problem_email)}" + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(body)
    )
    val intent = Intent(Intent.ACTION_SENDTO, mailUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))
    if (chooser.resolveActivity(packageManager) != null) {
        startActivity(chooser)
    } else {
        onError()
    }
}


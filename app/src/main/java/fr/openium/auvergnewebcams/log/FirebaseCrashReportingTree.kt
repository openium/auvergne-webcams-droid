package fr.openium.auvergnewebcams.log

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */

class FirebaseCrashReportingTree(private val firebaseCrashlytics: FirebaseCrashlytics) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            firebaseCrashlytics.log(message)
            t?.let { firebaseCrashlytics.recordException(it) }
        }
    }
}
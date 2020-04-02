package fr.openium.auvergnewebcams.utils

import fr.openium.auvergnewebcams.CustomApplication
import timber.log.Timber

object LogUtils {

    /**
     * Method that show a generic log when error is unknown
     */
    fun showSingleErrorLog(functionName: String, error: Throwable?) {
        Timber.e(error, "${CustomApplication.TAG} -> $functionName")
    }
}
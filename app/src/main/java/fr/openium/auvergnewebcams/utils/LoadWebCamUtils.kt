package fr.openium.auvergnewebcams.utils

import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by Openium on 19/02/2019.
 */
object LoadWebCamUtils {

    fun getMediaViewSurf(urlBase: String?): String {
        var media = ""
        if (!urlBase.isNullOrEmpty()) {
            try {
                val urlLD = String.format("%s/last", urlBase)
                val url = URL(urlLD)
                val bufferedReader = BufferedReader(InputStreamReader(url.openStream()))

                var line = bufferedReader.readLine()
                while (line != null && line.isNotBlank()) {
                    media += line
                    line = bufferedReader.readLine()
                }

                bufferedReader.close()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return media
    }
}
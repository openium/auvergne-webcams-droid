package fr.openium.auvergnewebcams.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by laura on 01/12/2017.
 */
object LoadWebCamUtils {

    fun getMediaViewSurf(urlBase: String?): String {
        var media = ""
        if (!urlBase.isNullOrEmpty()) {
            val urlLD = String.format("%s/last", urlBase)
            val url = URL(urlLD)
            val bufferedReader = BufferedReader(InputStreamReader(url.openStream()))

            var line = bufferedReader.readLine()
            while (line != null) {
                media += line
                line = bufferedReader.readLine()
            }

            bufferedReader.close()
        }
        return media
    }

    fun getLastUpdateWebcam(urlWebcam: String): Long {
        var lastUpdate = 0L
        if (urlWebcam.isNotEmpty()) {
            val url = URL(urlWebcam)
            val connection = url.openConnection()
            connection.connect()
            val lastModified = connection.getHeaderField("Last-Modified")
        //    Timber.e("date $urlWebcam => $lastModified")

            if (!lastModified.isNullOrEmpty()) {
                val dateFormat = SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US)
                dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                lastUpdate = dateFormat.parse(lastModified).time
            }
        }
        return lastUpdate
    }

}
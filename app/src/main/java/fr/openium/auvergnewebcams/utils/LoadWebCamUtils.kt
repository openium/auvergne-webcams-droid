package fr.openium.auvergnewebcams.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


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
}
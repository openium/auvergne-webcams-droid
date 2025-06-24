package fr.openium.auvergnewebcams.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by Openium on 19/02/2019.
 */
object LoadWebCamUtils {

    suspend fun getMediaViewSurf(urlBase: String?): String = withContext(Dispatchers.IO) {
        Timber.d("Loading ViewSurf url $urlBase")

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

                Timber.d("Load done for ViewSurf url $urlBase")
            } catch (e: Exception) {
                Timber.d("Error loading ViewSurf url $urlBase")
                Timber.e(e)
            }
        }
        media
    }

    suspend fun getMediaViewVideo(urlBase: String?): String = withContext(Dispatchers.IO) {
        Timber.d("Loading Video url $urlBase")

        var media = ""
        if (!urlBase.isNullOrEmpty()) {
            try {
                val url = URL(urlBase)
                val bufferedReader = BufferedReader(InputStreamReader(url.openStream()))

                var line = bufferedReader.readLine()
                while (line != null && line.isNotBlank()) {
                    media += line
                    line = bufferedReader.readLine()
                }

                bufferedReader.close()
                Timber.d("Load done for Video url $urlBase")
            } catch (e: Exception) {
                Timber.d("Error loading Video url $urlBase")
                Timber.e(e)
            }
        }
        media
    }
}
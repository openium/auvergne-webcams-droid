package fr.openium.auvergnewebcams.custom

import fr.openium.auvergnewebcams.repository.WebcamRepository
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class LastUpdateDateInterceptor(private val webcamRepository: WebcamRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val lastModified = response.header("Last-Modified")

        lastModified?.let {
            val url = chain.request().url.toString()
            val argsSplit = url.split("/")

            // Remove file extension for incoming search
            val urlMedia = argsSplit.lastOrNull()?.replace(".jpg", "") ?: ""

            Timber.d("TEST webcam refreshed")
            webcamRepository.updateLastUpdateDate(lastModified, urlMedia)
        }

        return response
    }
}
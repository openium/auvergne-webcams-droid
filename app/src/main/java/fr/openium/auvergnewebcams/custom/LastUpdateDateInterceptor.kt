package fr.openium.auvergnewebcams.custom

import fr.openium.auvergnewebcams.repository.WebcamRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class LastUpdateDateInterceptor(private val webcamRepository: WebcamRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val response = chain.proceed(chain.request())
        val lastModified = response.header("Last-Modified")

        lastModified?.let {
            val url = chain.request().url.toString()
            val argsSplit = url.split("/")

            // Remove file extension for incoming search
            val urlMedia = argsSplit.lastOrNull()?.replace(".jpg", "") ?: ""

            webcamRepository.updateLastUpdateDate(lastModified, urlMedia)
        }

        response
    }
}
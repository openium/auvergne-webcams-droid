package fr.openium.auvergnewebcams.custom

import fr.openium.auvergnewebcams.repository.WebcamRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class LastUpdateDateInterceptor(private val webcamRepository: WebcamRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val response = chain.proceed(chain.request())
            val lastModified = response.headers["last-modified"]

            if (lastModified != null) {
                webcamRepository.updateLastUpdateDate(lastModified, chain.request().url.toString())
            }
            response
        }
    }
}
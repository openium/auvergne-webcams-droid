package fr.openium.auvergnewebcams.custom

import android.os.AsyncTask
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideLoaderException
import com.github.piasy.biv.metadata.ImageInfoExtractor
import okhttp3.Response
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.File

class GetImageWebcamAsyncTask(var response: Response?, var callback: ImageLoader.Callback?, var externalCacheDir: File) :
    AsyncTask<Void, Void, File>() {

    override fun doInBackground(vararg params: Void?): File? {
        return response?.body?.let { body ->
            try {
                body.byteStream().source().buffer().use { source ->
                    val file = File(externalCacheDir, "lastWebcamDisplayedToUser.jpg")
                    file.sink().buffer().use { sink ->
                        sink.writeAll(source)
                        sink.flush()
                        file
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
    }

    override fun onPostExecute(file: File?) {
        super.onPostExecute(file)
        file?.let {
            callback?.onCacheHit(ImageInfoExtractor.getImageType(file), file)
            callback?.onSuccess(file)
        } ?: callback?.onFail(GlideLoaderException(null))
    }
}
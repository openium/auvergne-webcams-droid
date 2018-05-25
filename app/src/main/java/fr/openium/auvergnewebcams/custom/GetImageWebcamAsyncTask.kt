package fr.openium.auvergnewebcams.custom

import android.os.AsyncTask
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideLoaderException
import okhttp3.Response
import okio.Okio
import timber.log.Timber
import java.io.File

class GetImageWebcamAsyncTask(var response: Response?, var callback: ImageLoader.Callback?, var externalCacheDir: File) : AsyncTask<Void, Void, File>() {
    override fun doInBackground(vararg params: Void?): File? {
        response?.body()?.let { body ->
            try {
                Okio.buffer(Okio.source(body.byteStream())).use { source ->
                    val file = File(externalCacheDir, "webcam.jpg")
                    Okio.buffer(Okio.sink(file)).use { sink ->
                        sink.writeAll(source)
                        sink.flush()
                        return file
                    }
                }
            } catch (error: Exception) {
                Timber.e(error.message)
                return null
            }
        }
        return null
    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
    }

    override fun onPostExecute(file: File?) {
        super.onPostExecute(file)
        if (file != null) {
            callback?.onCacheHit(file)
            callback?.onSuccess(file)
        } else {
            callback?.onFail(GlideLoaderException(null))
        }
    }
}
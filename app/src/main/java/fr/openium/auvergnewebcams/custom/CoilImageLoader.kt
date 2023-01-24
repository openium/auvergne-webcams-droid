package fr.openium.auvergnewebcams.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.metadata.ImageInfoExtractor

internal class CoilLoaderException(val errorDrawable: Drawable?) : RuntimeException()

internal class CoilImageLoader(
    private val context: Context
) : ImageLoader {
    private val pendingRequestTargets: MutableMap<Int, Disposable> = HashMap(3)

    override fun loadImage(requestId: Int, uri: Uri?, callback: ImageLoader.Callback?) {
        context.imageLoader.diskCache?.get(uri.toString())?.use { snapshot ->
            val imageFile = snapshot.data.toFile()
            callback?.onCacheHit(ImageInfoExtractor.getImageType(imageFile), imageFile)
            callback?.onSuccess(imageFile)
        } ?: fetchImage(requestId, uri, callback)
    }

    override fun prefetch(uri: Uri?) {
        val request = ImageRequest
            .Builder(context)
            .data(uri)
            .diskCacheKey(uri.toString())
            .build()
        context.imageLoader.enqueue(request)
    }

    override fun cancel(requestId: Int) {
        pendingRequestTargets.remove(requestId)
    }

    override fun cancelAll() {
        pendingRequestTargets.keys.forEach { key ->
            dispose(key)
        }
    }

    private fun fetchImage(requestId: Int, uri: Uri?, callback: ImageLoader.Callback?) {
        val request = ImageRequest
            .Builder(context)
            .data(uri)
            .diskCacheKey(uri.toString())
            .target(
                onStart = {
                    callback?.onStart()
                },
                onSuccess = {
                    context.imageLoader.diskCache?.get(uri.toString())?.use { snapshot ->
                        val imageFile = snapshot.data.toFile()
                        callback?.onCacheMiss(ImageInfoExtractor.getImageType(imageFile), imageFile)
                        callback?.onFinish()
                        callback?.onSuccess(imageFile)
                    }
                },
                onError = { errorDrawable ->
                    callback?.onFinish()
                    callback?.onFail(CoilLoaderException(errorDrawable))
                }
            )
            .build()
        dispose(requestId)
        val disposable = context.imageLoader.enqueue(request)
        remember(requestId, disposable)
    }

    private fun dispose(requestId: Int) {
        pendingRequestTargets.remove(requestId)?.dispose()
    }

    private fun remember(requestId: Int, disposable: Disposable) {
        pendingRequestTargets[requestId] = disposable
    }
}
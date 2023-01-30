package fr.openium.auvergnewebcams.custom

import com.github.piasy.biv.loader.ImageLoader
import java.io.File

open class SimpleImageLoaderCallback : ImageLoader.Callback {
    override fun onCacheHit(imageType: Int, image: File) {}
    override fun onCacheMiss(imageType: Int, image: File) {}
    override fun onStart() {}
    override fun onProgress(progress: Int) {}
    override fun onFinish() {}
    override fun onSuccess(image: File) {}
    override fun onFail(error: Exception) {}
}
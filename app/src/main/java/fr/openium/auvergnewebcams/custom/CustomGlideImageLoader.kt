package fr.openium.auvergnewebcams.custom

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideLoaderException
import com.github.piasy.biv.loader.glide.GlideProgressSupport
import okhttp3.*
import timber.log.Timber
import java.io.IOException


/**
 * Created by Openium on 19/02/2019.
 */
class CustomGlideImageLoader private constructor(val context: Context, okHttpClient: OkHttpClient? = null) : ImageLoader {

    private val okHttpClient: OkHttpClient = okHttpClient ?: OkHttpClient.Builder().build()

    init {
        GlideProgressSupport.init(Glide.get(context), okHttpClient)
    }

    override fun loadImage(requestId: Int, uri: Uri?, callback: ImageLoader.Callback?) {

        okHttpClient.newCall(Request.Builder().get().url(uri.toString()).build()).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Timber.e(e)
                callback?.onFail(GlideLoaderException(null))
            }

            override fun onResponse(call: Call, response: Response) {
                callback?.onFinish()

                context.externalCacheDir?.let {
                    GetImageWebcamAsyncTask(response, callback, it).execute()
                }
            }
        })
    }

    override fun prefetch(uri: Uri) {}
    override fun cancel(requestId: Int) {}
    override fun cancelAll() {}

    companion object {

        @JvmOverloads
        fun with(context: Context, okHttpClient: OkHttpClient? = null): CustomGlideImageLoader =
            CustomGlideImageLoader(context, okHttpClient)
    }
}
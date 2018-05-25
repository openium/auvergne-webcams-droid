package fr.openium.auvergnewebcams.utils

import android.content.Context
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideLoaderException
import com.github.piasy.biv.loader.glide.GlideProgressSupport
import com.github.piasy.biv.view.BigImageView
import fr.openium.auvergnewebcams.custom.GetImageWebcamAsyncTask
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.injection.GlideRequests
import okhttp3.*
import java.io.IOException

/**
 * Created by t.coulange on 16/11/2017.
 */
class CustomGlideImageLoader private constructor(val context: Context, okHttpClient: OkHttpClient? = null) : ImageLoader {

    private val mRequestManager: GlideRequests
    private val okHttpClient: OkHttpClient

    init {
        mRequestManager = GlideApp.with(context)
        this.okHttpClient = okHttpClient ?: OkHttpClient.Builder().build()
        GlideProgressSupport.init(Glide.get(context), okHttpClient)
    }

    override fun cancel(requestId: Int) {

    }

    override fun loadImage(requestId: Int, uri: Uri?, callback: ImageLoader.Callback?) {

        okHttpClient.newCall(Request.Builder().get().url(uri.toString()).build()).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback?.onFail(GlideLoaderException(null))
            }

            override fun onResponse(call: Call?, response: Response?) {
                callback?.onFinish()

                GetImageWebcamAsyncTask(response, callback, context.externalCacheDir).execute()
            }
        })
    }

    override fun showThumbnail(parent: BigImageView, thumbnail: Uri, scaleType: Int): View {
        return View(context)
    }

    override fun prefetch(uri: Uri) {

    }

    companion object {

        @JvmOverloads
        fun with(context: Context, okHttpClient: OkHttpClient? = null): CustomGlideImageLoader {
            return CustomGlideImageLoader(context, okHttpClient)
        }
    }
}

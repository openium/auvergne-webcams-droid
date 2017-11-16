package fr.openium.auvergnewebcams.utils

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideLoaderException
import com.github.piasy.biv.loader.glide.GlideProgressSupport
import com.github.piasy.biv.loader.glide.R
import com.github.piasy.biv.view.BigImageView
import fr.openium.auvergnewebcams.injection.GlideApp
import fr.openium.auvergnewebcams.injection.GlideRequests
import okhttp3.*
import okio.Okio
import java.io.File
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

    override fun loadImage(uri: Uri, callback: ImageLoader.Callback) {
        okHttpClient.newCall(Request.Builder().get().url(uri.toString()).build()).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.onFail(GlideLoaderException(null))
            }

            override fun onResponse(call: Call?, response: Response?) {
                callback.onFinish()
                object : AsyncTask<Void, Void, File?>() {
                    override fun doInBackground(vararg p0: Void?): File? {
                        if (response?.body() != null) {
                            Okio.buffer(Okio.source(response.body()!!.byteStream())).use { source ->
                                val file = File(context.externalCacheDir, "webcam.jpg")
                                Okio.buffer(Okio.sink(file)).use { sink ->
                                    sink.writeAll(source)
                                    sink.flush()
                                    return file
                                }
                            }
                        }
                        return null
                    }

                    override fun onPostExecute(file: File?) {
                        if (file != null) {
                            callback.onCacheHit(file)
                            callback.onSuccess(file)
                        } else {
                            callback.onFail(GlideLoaderException(null))
                        }

                    }

                }.execute()
            }
        })

//        Glide.get(co)

//        mRequestManager
//                .downloadOnly()
//                .load(uri)
//                .into(object : ImageDownloadTarget(uri.toString()) {
//                    override fun onResourceReady(resource: File,
//                                                 transition: Transition<in File>) {
//                        // we don't need delete this image file, so it behaves live cache hit
//                        callback.onCacheHit(resource)
//                        callback.onSuccess(resource)
//                    }
//
//                    override fun onLoadFailed(errorDrawable: Drawable?) {
//
//                    }
//
//                    override fun onDownloadStart() {
//                        callback.onStart()
//                    }
//
//                    override fun onProgress(progress: Int) {
//                        callback.onProgress(progress)
//                    }
//
//                    override fun onDownloadFinish() {
//                        callback.onFinish()
//                    }
//                })
    }

    override fun showThumbnail(parent: BigImageView, thumbnail: Uri, scaleType: Int): View {
        val thumbnailView = LayoutInflater.from(parent.context)
                .inflate(R.layout.ui_glide_thumbnail, parent, false) as ImageView
        when (scaleType) {
            BigImageView.INIT_SCALE_TYPE_CENTER_CROP -> thumbnailView.scaleType = ImageView.ScaleType.CENTER_CROP
            BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE -> thumbnailView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            else -> {
            }
        }
        mRequestManager
                .load(thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(thumbnailView)
        return thumbnailView
    }

    override fun prefetch(uri: Uri) {
//        mRequestManager
//                .downloadOnly()
//                .load(uri)
//                .into(object : SimpleTarget<File>() {
//                    override fun onResourceReady(resource: File,
//                                                 transition: Transition<in File>) {
//                        // not interested in result
//                    }
//                })
    }

    companion object {

        @JvmOverloads
        fun with(context: Context, okHttpClient: OkHttpClient? = null): CustomGlideImageLoader {
            return CustomGlideImageLoader(context, okHttpClient)
        }
    }
}

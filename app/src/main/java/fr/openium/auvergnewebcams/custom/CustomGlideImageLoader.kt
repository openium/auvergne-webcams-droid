package fr.openium.auvergnewebcams.custom

/**
 * Created by Openium on 19/02/2019.
 */
//class CustomGlideImageLoader private constructor(val context: Context, okHttpClient: OkHttpClient? = null) : ImageLoader {
//
//    private val okHttpClient: OkHttpClient = okHttpClient ?: OkHttpClient.Builder().build()
//
//    init {
//        GlideProgressSupport.init(Glide.get(context), okHttpClient)
//    }
//
//    override fun cancel(requestId: Int) {}
//
//    override fun loadImage(requestId: Int, uri: Uri?, callback: ImageLoader.Callback?) {
//        okHttpClient.newCall(Request.Builder().get().url(uri.toString()).build()).enqueue(object : Callback {
//
//            override fun onFailure(call: Call?, e: IOException?) {
//                callback?.onFail(GlideLoaderException(null))
//            }
//
//            override fun onResponse(call: Call?, response: Response?) {
//                callback?.onFinish()
//
//                context.externalCacheDir?.let {
//                    GetImageWebcamAsyncTask(response, callback, it).execute()
//                }
//            }
//        })
//    }
//
//    override fun showThumbnail(parent: BigImageView, thumbnail: Uri, scaleType: Int): View {
//        return View(context)
//    }
//
//    override fun prefetch(uri: Uri) {}
//
//    companion object {
//
//        @JvmOverloads
//        fun with(context: Context, okHttpClient: OkHttpClient? = null): CustomGlideImageLoader {
//            return CustomGlideImageLoader(context, okHttpClient)
//        }
//    }
//}

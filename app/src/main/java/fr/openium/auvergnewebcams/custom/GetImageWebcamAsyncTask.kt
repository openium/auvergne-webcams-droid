package fr.openium.auvergnewebcams.custom

//class GetImageWebcamAsyncTask(var response: Response?, var callback: ImageLoader.Callback?, var externalCacheDir: File) :
//    AsyncTask<Void, Void, File>() {
//
//    override fun doInBackground(vararg params: Void?): File? {
//        response?.body()?.let { body ->
//            try {
//                Okio.buffer(Okio.source(body.byteStream())).use { source ->
//                    val file = File(externalCacheDir, "webcam.jpg")
//                    Okio.buffer(Okio.sink(file)).use { sink ->
//                        sink.writeAll(source)
//                        sink.flush()
//                        return file
//                    }
//                }
//            } catch (e: Exception) {
//                Timber.e(e)
//            }
//        }
//        return null
//    }
//
//    override fun onPostExecute(file: File?) {
//        super.onPostExecute(file)
//        if (file != null) {
//            callback?.onCacheHit(file)
//            callback?.onSuccess(file)
//        } else {
//            callback?.onFail(GlideLoaderException(null))
//        }
//    }
//}
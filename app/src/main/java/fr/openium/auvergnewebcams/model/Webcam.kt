package fr.openium.auvergnewebcams.model

import fr.openium.auvergnewebcams.utils.DateUtils
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by t.coulange on 09/12/2016.
 */
@RealmClass
open class Webcam(@PrimaryKey var uid: Long = 0, var order: Int = 0, var title: String? = null, var imageLD: String? = null, var imageHD: String? = null,
                  var viewsurfLD: String? = null, var viewsurfHD: String? = null, var type: String? = "", var tags: RealmList<String>? = RealmList(),
                  var mediaViewSurfLD: String? = null, var mediaViewSurfHD: String? = null, var lastUpdate: Long? = null, var isFavoris: Boolean = false, var hidden: Boolean? = false) : RealmObject() {

    enum class WEBCAM_TYPE(val nameType: String) {

        IMAGE("image"),
        VIEWSURF("viewsurf")

    }


    fun getUrlForWebcam(canBeHD: Boolean, canBeVideo: Boolean): String {
        var url = ""
        if (type == Webcam.WEBCAM_TYPE.VIEWSURF.nameType) {
            val format: String
            if (canBeVideo) {
                format = "%s/%s.mp4"
            } else {
                format = "%s/%s.jpg"
            }
            if (canBeHD && !mediaViewSurfHD.isNullOrEmpty() && !viewsurfHD.isNullOrEmpty()) {
                url = String.format(format, viewsurfHD!!, mediaViewSurfHD!!)
            } else if (!mediaViewSurfLD.isNullOrEmpty() && !viewsurfLD.isNullOrEmpty()) {
                url = String.format(format, viewsurfLD!!, mediaViewSurfLD!!)
            }
        } else {
            if (canBeHD && !imageHD.isNullOrBlank()) {
                url = imageHD!!
            } else if (!imageLD.isNullOrBlank()) {
                url = imageLD!!
            }
        }
        return url
    }

    fun isUpToDate(): Boolean {
        var isUp = true
        //Timber.e("last date => $lastUpdate   name => $title")
        if (lastUpdate != null && lastUpdate!! > 0L) {
            isUp = !DateUtils.isMoreThan48Hour(lastUpdate!!)
        }
        return isUp
    }

}
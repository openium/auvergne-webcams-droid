package fr.openium.auvergnewebcams.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Openium on 19/02/2019.
 */
@Entity
class Webcam {
    @PrimaryKey
    var uid: Long = 0
    var sectionUid: Long? = null
    var order: Int = 0
    var title: String? = null
    var imageLD: String? = null
    var imageHD: String? = null
    var viewsurfLD: String? = null
    var viewsurfHD: String? = null
    var type: String? = null
    var tags: List<String>? = listOf()
    var mediaViewSurfLD: String? = null
    var mediaViewSurfHD: String? = null
    var lastUpdate: Long? = null
    var isFavoris: Boolean = false
    var hidden: Boolean? = false

    fun populateId(uid: Long) {
        sectionUid = uid
    }

    fun getUrlForWebcam(canBeHD: Boolean, canBeVideo: Boolean): String =
        if (type == WebcamType.VIEWSURF.nameType) {
            val format = if (canBeVideo) "%s/%s.mp4" else "%s/%s.jpg"

            // Load LD/HD video
            if (canBeHD && !mediaViewSurfHD.isNullOrEmpty() && !viewsurfHD.isNullOrEmpty()) {
                String.format(format, viewsurfHD, mediaViewSurfHD)
            } else String.format(format, viewsurfLD, mediaViewSurfLD)
        } else {
            // Load LD/HD image
            if (canBeHD && !imageHD.isNullOrBlank()) imageHD ?: "" else imageLD ?: ""
        }

    fun getLastUpdateDate(): Long? =
        if (lastUpdate != null && lastUpdate != 0L) {
            lastUpdate
        } else null

    enum class WebcamType(val nameType: String) {
        IMAGE("image"),
        VIEWSURF("viewsurf")
    }
}
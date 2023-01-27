package fr.openium.auvergnewebcams.ext

import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.model.entity.Webcam
import timber.log.Timber


fun Webcam.getUrlForWebcam(canBeHD: Boolean, canBeVideo: Boolean): String =
    when (type) {
        WebcamType.VIEWSURF.jsonKey -> {
            val format = if (canBeVideo) "%s/%s.mp4" else "%s/%s.jpg"

            // Load viewsurf
            val url = if (canBeHD && !mediaViewSurfHD.isNullOrEmpty()) {
                String.format(format, viewsurf, mediaViewSurfHD)
            } else String.format(format, viewsurf, mediaViewSurfLD)

            Timber.d("URL ViewSurf $url")

            url
        }

        WebcamType.VIDEO.jsonKey -> {
            // Load video
            Timber.d("URL Video $video")

            "$video.mp4" ?: ""
        }

        else -> {
            // Load LD/HD image
            if (canBeHD && !imageHD.isNullOrBlank()) imageHD ?: "" else imageLD ?: ""
        }
    }

val Webcam.lastUpdateDate: Long?
    get() = lastUpdate?.let {
        if (it > 0L) lastUpdate else null
    }

fun Webcam.populateId(sectionID: Long) {
    sectionUid = sectionID
}
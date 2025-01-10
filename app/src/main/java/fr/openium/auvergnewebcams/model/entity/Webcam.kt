package fr.openium.auvergnewebcams.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Openium on 19/02/2019.
 */
@Entity
data class Webcam(
    @PrimaryKey
    var uid: Long = 0,
    var sectionUid: Long? = null,
    var order: Int? = null,
    var title: String? = null,
    var imageLD: String? = null,
    var imageHD: String? = null,
    var viewsurf: String? = null,
    var video: String? = null,
    var type: String? = null,
    var tags: List<String>? = null,
    var mediaViewSurfLD: String? = null,
    var mediaViewSurfHD: String? = null,
    var lastUpdate: Long? = null,
    var hidden: Boolean? = null,
    var isFavorite: Boolean = false,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var mapImageName: String? = null,
)
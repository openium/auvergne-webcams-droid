package fr.openium.auvergnewebcams.model.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by Openium on 19/02/2019.
 */
@Entity
class Section {

    @PrimaryKey
    var uid: Long = 0

    var order: Int = 0

    var title: String? = null

    var imageName: String? = null

    var latitude: Double = 0.0

    var longitude: Double = 0.0

    @Ignore
    var webcams: List<Webcam> = listOf()
}
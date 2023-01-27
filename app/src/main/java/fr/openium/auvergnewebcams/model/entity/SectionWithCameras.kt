package fr.openium.auvergnewebcams.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SectionWithCameras(
    @Embedded
    val section: Section,
    @Relation(
        parentColumn = "uid",
        entityColumn = "sectionUid"
    )
    val webcams : List<Webcam> = emptyList()
)
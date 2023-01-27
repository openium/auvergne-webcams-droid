package fr.openium.auvergnewebcams.ext

import fr.openium.auvergnewebcams.enums.WebcamType

val WebcamType.jsonKey: String
    get() = when (this) {
        WebcamType.IMAGE ->
            "image"

        WebcamType.VIEWSURF ->
            "viewsurf"

        WebcamType.VIDEO ->
            "video"
    }
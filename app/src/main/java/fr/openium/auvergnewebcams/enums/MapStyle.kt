package fr.openium.auvergnewebcams.enums

import com.mapbox.maps.Style

enum class MapStyle(val style: String) {
    DARK(Style.DARK),
    LIGHT(Style.LIGHT),
    OUTSIDE(Style.OUTDOORS),
    ROADS(Style.MAPBOX_STREETS),
    SATELLITE(Style.SATELLITE);

    companion object {
        fun from(style: String): MapStyle? =
            MapStyle.values().firstOrNull { it.style == style }
    }
}
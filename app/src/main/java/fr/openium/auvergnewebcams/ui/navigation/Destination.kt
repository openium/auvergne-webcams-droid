package fr.openium.auvergnewebcams.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {

    @Serializable
    data object Splash : Destination

    @Serializable
    data object Main : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data object About : Destination

    @Serializable
    data class SectionDetails(val sectionId: Long) : Destination

    @Serializable
    data object Search : Destination

    @Serializable
    data class Map(val sectionId: Long?, val sectionTitle: String?) : Destination

    @Serializable
    data class WebcamDetails(val webcamId: Long) : Destination


}


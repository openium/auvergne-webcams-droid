package fr.openium.auvergnewebcams.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {

    @kotlinx.serialization.Serializable
    data object Splash : Destination

    @Serializable
    data object Main : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data object About : Destination

    @Serializable
    data class SectionDetails(val sectionID: Long) : Destination

    @Serializable
    data object Search : Destination

    @Serializable
    data object Map : Destination

    @Serializable
    data object MapSection : Destination

    @Serializable
    data class WebcamDetails(val webcamID: Long) : Destination


}


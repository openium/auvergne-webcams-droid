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


}


package fr.openium.auvergnewebcams.event

import com.jakewharton.rxrelay2.PublishRelay

/**
 * Created by Skyle on 19/02/2019.
 */

val eventCameraFavoris = PublishRelay.create<Long>()

val eventNewValueDelay = PublishRelay.create<Int>()
package fr.openium.auvergnewebcams.event

import com.jakewharton.rxrelay2.PublishRelay

/**
 * Created by Openium on 19/02/2019.
 */

val eventHasNetwork = PublishRelay.create<Boolean>()

val eventCameraFavoris = PublishRelay.create<Long>() // TODO

val eventNewRefreshDelayValue = PublishRelay.create<Int>()

val eventRefreshDelayValueChanged = PublishRelay.create<Unit>()
package fr.openium.auvergnewebcams.event

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay

/**
 * Created by Openium on 19/02/2019.
 */

val eventHasNetwork = BehaviorRelay.create<Boolean>()

val eventCameraFavoris = PublishRelay.create<Long>()

val eventNewRefreshDelayValue = PublishRelay.create<Int>()
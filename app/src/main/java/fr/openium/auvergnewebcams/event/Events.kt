package fr.openium.auvergnewebcams.event

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay

/**
 * Created by Openium on 19/02/2019.
 */

val eventHasNetwork: BehaviorRelay<Boolean> =
    BehaviorRelay.create()

val eventCameraFavoris = PublishRelay.create<Long>() // TODO

val eventRefreshDelayValueChanged = PublishRelay.create<Unit>()
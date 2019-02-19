package fr.openium.auvergnewebcams.model

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by Skyle on 19/02/2019.
 */
@RealmClass
open class Weather(var id: Long? = 0, var temp: Float? = 0f, var lon: Double = 0.0, var lat: Double = 0.0) : RealmObject()
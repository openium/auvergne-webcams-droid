package fr.openium.auvergnewebcams.model

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Created by laura on 20/03/2017.
 */
@RealmClass
open class Weather(var id: Long? = 0, var temp: Float? = 0f) : RealmObject() {

}
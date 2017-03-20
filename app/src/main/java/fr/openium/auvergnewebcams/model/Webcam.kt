package fr.openium.auvergnewebcams.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by t.coulange on 09/12/2016.
 */
@RealmClass
open class Webcam(@PrimaryKey var uid: Long = 0, var order:Int = 0, var title: String? = null, var imageLD: String? = null, var imageHD: String? = null,
                  var lowQualityOnly: Boolean = false) : RealmObject() {

}
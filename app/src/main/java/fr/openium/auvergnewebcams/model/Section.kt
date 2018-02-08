package fr.openium.auvergnewebcams.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by laura on 20/03/2017.
 */
@RealmClass
open class Section(@PrimaryKey var uid: Long = 0, var order: Int = 0, var title: String? = null, var imageName: String? = null, var latitude: Double = 0.0, var longitude: Double = 0.0,
                   var webcams: RealmList<Webcam> = RealmList()) : RealmObject() {

}
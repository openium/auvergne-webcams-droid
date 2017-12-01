package fr.openium.auvergnewebcams.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by t.coulange on 09/12/2016.
 */
@RealmClass
open class Webcam(@PrimaryKey var uid: Long = 0, var order: Int = 0, var title: String? = null, var imageLD: String? = null, var imageHD: String? = null,
                  var viewsurfLD: String? = null, var viewsurfHD: String? = null, var type: String? = "",
                  var tags: RealmList<String>? = RealmList(), var mediaViewSurfLD: String? = null, var mediaViewSurfHD: String? = null) : RealmObject() {

    enum class WEBCAM_TYPE(val nameType: String) {

        IMAGE("image"),
        VIEWSURF("viewsurf")

    }
}
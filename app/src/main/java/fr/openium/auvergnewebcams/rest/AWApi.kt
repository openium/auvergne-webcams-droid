package fr.openium.auvergnewebcams.rest

import fr.openium.auvergnewebcams.rest.model.SectionList
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by Openium on 19/02/2019.
 */
interface AWApi {

    @GET("resources/json/v4/aw-config.json")
    fun getSections(): Single<SectionList> {
        error("Not implemented")
    }
}
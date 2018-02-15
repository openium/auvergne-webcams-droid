package fr.openium.auvergnewebcams.rest

import fr.openium.auvergnewebcams.model.SectionList
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET

/**
 * Created by laura on 01/12/2017.
 */
interface AWApi {

    @GET("resources/json/v2/aw-config.json")
    fun getSections(): Single<Result<SectionList>> {
        error("Not implemented")
    }

}
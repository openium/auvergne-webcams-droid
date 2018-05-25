package fr.openium.auvergnewebcams.rest

import fr.openium.auvergnewebcams.model.rest.WeatherRest
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by laura on 01/12/2017.
 */
interface AWWeatherApi {

    @GET("weather")
    fun queryByGeographicCoordinates(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") appid: String): Single<Result<WeatherRest>> {
        error("Not implemented")
    }

}
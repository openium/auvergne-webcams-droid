package fr.openium.auvergnewebcams.rest

import fr.openium.auvergnewebcams.rest.model.weather.WeatherRest
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Openium on 19/02/2019.
 */
interface AWWeatherApi {

    @GET("weather")
    fun queryByGeographicCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
    ): Single<Response<WeatherRest>>
}
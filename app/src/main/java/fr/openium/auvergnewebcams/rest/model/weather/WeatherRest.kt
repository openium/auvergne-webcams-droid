package fr.openium.auvergnewebcams.rest.model.weather

/**
 * Created by Openium on 19/02/2019.
 */
data class WeatherRest(val coord: WeatherCoord? = null, val weather: List<WeatherInfo>? = null, val main: WeatherTemp? = null)
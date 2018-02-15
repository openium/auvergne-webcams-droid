package fr.openium.auvergnewebcams.model.rest

/**
 * Created by godart on 31/01/2018.
 */
data class WeatherRest(val coord: WeatherCoord? = null, val weather: List<WeatherInfo>? = null, val main: WeatherTemp? = null)
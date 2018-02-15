package fr.openium.auvergnewebcams.utils

import fr.openium.auvergnewebcams.R

/**
 * Created by laura on 07/12/2017.
 */
object WeatherUtils {

    fun weatherImage(weatherUID: Long): Int {
        if (weatherUID >= 500L && weatherUID < 600L) {
            return R.drawable.weather_rain
        } else if (weatherUID >= 200L && weatherUID < 300L) {
            return R.drawable.weather_thunderstorm
        } else if (weatherUID >= 600L && weatherUID < 700L) {
            return R.drawable.weather_snow
        } else if (weatherUID == 800L) {
            return R.drawable.weather_sun
        } else if (weatherUID == 801L || weatherUID == 802L || weatherUID == 803L) {
            return R.drawable.weather_cloudy
        } else {
            return R.drawable.weather_cloud
        }
    }

    fun convertKelvinToCelcius(temp: Float): Int {
        return Math.round(temp - 273.15f)
    }

}
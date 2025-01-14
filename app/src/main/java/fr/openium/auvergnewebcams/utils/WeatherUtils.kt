package fr.openium.auvergnewebcams.utils

import fr.openium.auvergnewebcams.R

object WeatherUtils {

    fun weatherImage(weatherUid: Long): Int = when (weatherUid) {
        in 500L..599 -> R.drawable.ic_weather_rain

        in 200L..299 -> R.drawable.ic_weather_thunderstorm

        in 600L..699 -> R.drawable.ic_weather_snow

        800L -> R.drawable.ic_weather_sun

        in 801L..803 -> R.drawable.ic_weather_cloudy

        else -> R.drawable.ic_weather_cloud
    }

    fun convertKelvinToCelsius(temp: Float): Int {
        if (temp.compareTo(0.0) != 0) {
            return (temp - 273.15f).toInt()
        }
        return 0
    }
}
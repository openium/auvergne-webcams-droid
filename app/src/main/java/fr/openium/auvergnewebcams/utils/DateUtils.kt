package fr.openium.auvergnewebcams.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by laura on 07/12/2017.
 */
object DateUtils {

    private const val HOUR_48_MILLISECONDS = 172800000

    private val dateFormatDateHour = SimpleDateFormat("dd.MM.yy - HH'h'mm", Locale.getDefault())

    fun getDateFormatDateHour(date: Long): String {
        return dateFormatDateHour.format(Date(date))
    }

    fun isMoreThan48Hour(time: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - time
        return diff > HOUR_48_MILLISECONDS
    }
}
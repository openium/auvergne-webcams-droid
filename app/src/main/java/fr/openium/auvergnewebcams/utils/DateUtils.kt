package fr.openium.auvergnewebcams.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by laura on 07/12/2017.
 */
object DateUtils {

    private val dateFormatDateHour = SimpleDateFormat("dd.MM.yy - HH'h'mm", Locale.getDefault())

    fun getDateFormatDateHour(date: Long): String {
        return dateFormatDateHour.format(Date(date))
    }

}
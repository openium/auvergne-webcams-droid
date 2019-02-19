package fr.openium.auvergnewebcams.utils

import android.content.Context
import fr.openium.auvergnewebcams.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Skyle on 19/02/2019.
 */
object DateUtils {

    private const val MILLISECONDS_OF_48_HOURS = 172800000L

    private lateinit var dateFullFormat: SimpleDateFormat

    fun init(context: Context) {
        dateFullFormat = SimpleDateFormat(context.getString(R.string.date_full_format), Locale.getDefault())
    }

    fun getDateInFullFormat(date: Long): String {
        return dateFullFormat.format(Date(date))
    }

    fun isMoreThan48Hour(time: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - time
        return diff > MILLISECONDS_OF_48_HOURS
    }
}
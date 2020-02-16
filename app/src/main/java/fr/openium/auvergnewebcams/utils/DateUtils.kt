package fr.openium.auvergnewebcams.utils

import android.content.Context
import fr.openium.auvergnewebcams.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Openium on 19/02/2019.
 */
class DateUtils(context: Context) {

    private var dateFullFormat: SimpleDateFormat =
        SimpleDateFormat(context.getString(R.string.date_full_format), Locale.getDefault())
    private var dateFormatGMT: SimpleDateFormat =
        SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US).apply { timeZone = TimeZone.getTimeZone("GMT") }

    fun getDateInFullFormat(date: Long): String = dateFullFormat.format(Date(date))

    fun isMoreThan48Hour(time: Long): Boolean = System.currentTimeMillis() - time > 2 * 24 * 60 * 60 * 1000

    fun parseDateGMT(date: String): Long? = try {
        dateFormatGMT.parse(date)?.time
    } catch (e: Exception) {
        Timber.e(e, "date $date")
        null
    }
}
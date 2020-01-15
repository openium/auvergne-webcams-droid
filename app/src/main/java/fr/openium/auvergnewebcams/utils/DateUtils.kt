package fr.openium.auvergnewebcams.utils

import android.content.Context
import fr.openium.auvergnewebcams.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Openium on 19/02/2019.
 */
object DateUtils {

    private lateinit var dateFullFormat: SimpleDateFormat
    private lateinit var dateFormatGMT: SimpleDateFormat

    fun init(context: Context) {
        dateFullFormat = SimpleDateFormat(context.getString(R.string.date_full_format), Locale.getDefault())
        dateFormatGMT = SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US)
        dateFormatGMT.timeZone = TimeZone.getTimeZone("GMT")
    }

    fun getDateInFullFormat(date: Long): String = dateFullFormat.format(Date(date))

    fun isMoreThan48Hour(time: Long): Boolean = System.currentTimeMillis() - time > 2 * 24 * 60 * 60 * 1000

    fun parseDateGMT(date: String): Long? = dateFormatGMT.parse(date)?.time
}
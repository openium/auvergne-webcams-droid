package fr.openium.auvergnewebcams.ext

import fr.openium.auvergnewebcams.utils.DateUtils
import java.util.*

/**
 * Created by Openium on 19/02/2019.
 */

fun Date.toFullFormatString() = DateUtils.getDateInFullFormat(time)
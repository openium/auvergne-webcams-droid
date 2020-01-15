package fr.openium.auvergnewebcams.ext

/**
 * Created by Openium on 19/02/2019.
 */

inline fun String.ifNotEmpty(defaultValue: () -> Any?): Any? = if (isNotEmpty()) defaultValue() else this
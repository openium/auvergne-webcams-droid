package fr.openium.auvergnewebcams.model.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringTypeConverter {

    @TypeConverter
    fun toString(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<String>? {
        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }
}
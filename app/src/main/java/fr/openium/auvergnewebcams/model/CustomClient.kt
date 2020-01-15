package fr.openium.auvergnewebcams.model

import android.content.Context
import androidx.room.Room


class CustomClient private constructor(context: Context) {

    //App database object
    val database: CustomDatabase = Room.databaseBuilder(context, CustomDatabase::class.java, "CustomDatabase").build()

    companion object {
        private var instance: CustomClient? = null

        @Synchronized
        fun getInstance(context: Context): CustomClient {
            if (instance == null) {
                instance = CustomClient(context)
            }
            return instance!!
        }
    }
}
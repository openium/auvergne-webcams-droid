package fr.openium.auvergnewebcams.model

import android.content.Context
import androidx.room.Room


class AWClient private constructor(context: Context) {

    // App database object
    val database: AWDatabase = Room.databaseBuilder(context, AWDatabase::class.java, "AWDatabase").build()

    companion object {
        private var instance: AWClient? = null

        @Synchronized
        fun getInstance(context: Context): AWClient {
            if (instance == null) {
                instance = AWClient(context)
            }
            return instance!!
        }
    }
}
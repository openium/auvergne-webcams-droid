package fr.openium.auvergnewebcams.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.openium.auvergnewebcams.model.converter.StringTypeConverter
import fr.openium.auvergnewebcams.model.dao.SectionDao
import fr.openium.auvergnewebcams.model.dao.WebcamDao
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.Webcam

@Database(entities = [Section::class, Webcam::class], version = 4)
@TypeConverters(StringTypeConverter::class)
abstract class AWDatabase : RoomDatabase() {

    abstract fun sectionDao(): SectionDao

    abstract fun webcamDao(): WebcamDao
}
package fr.openium.auvergnewebcams.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.model.entity.SectionWithCameras
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {

    // Query

    @Query("SELECT * FROM Section WHERE uid = :sectionId LIMIT 1")
    suspend fun getSectionWithCameras(sectionId: Long): SectionWithCameras

    @Query("SELECT * FROM Section")
    suspend fun getSections(): List<Section>

    @Query("SELECT * FROM Section ORDER BY `order`")
    fun watchSectionsWithCameras(): Flow<List<SectionWithCameras>>

    // Update

    @Update
    suspend fun update(section: Section): Int

    @Update
    suspend fun update(sections: List<Section>): Int

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(section: Section): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sections: List<Section>): List<Long>

    // Delete

    @Delete
    suspend fun delete(section: Section)

    @Delete
    suspend fun delete(sections: List<Section>)

    @Query("DELETE FROM Section WHERE uid NOT IN (:map)")
    suspend fun deleteAllNotInUids(map: List<Long>)
}
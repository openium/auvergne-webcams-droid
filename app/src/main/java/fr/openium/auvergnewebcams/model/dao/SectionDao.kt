package fr.openium.auvergnewebcams.model.dao

import androidx.room.*
import fr.openium.auvergnewebcams.model.entity.Section
import io.reactivex.Completable
import io.reactivex.Observable


@Dao
interface SectionDao {

    // Queries

    @Query("SELECT * FROM Section")
    fun getSections(): List<Section>

    @Query("SELECT * FROM Section")
    fun getSectionsObs(): Observable<List<Section>>

    // Updates

    @Update
    fun update(section: Section): Int

    @Update
    fun update(sections: List<Section>): Int

    // Inserts

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(section: Section): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sections: List<Section>): List<Long>

    // Deletes

    @Delete
    fun delete(section: Section)

    @Delete
    fun delete(sections: List<Section>)

    @Query("DELETE FROM Section WHERE uid NOT IN (:map)")
    fun deleteAllNotInUids(map: List<Long>): Completable
}
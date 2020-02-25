package fr.openium.auvergnewebcams.model.dao

import androidx.room.*
import fr.openium.auvergnewebcams.model.entity.Section
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


@Dao
interface SectionDao {

    // Query

    @Query("SELECT * FROM Section WHERE uid == :sectionId LIMIT 1")
    fun getSectionSingle(sectionId: Long): Single<Section>

    @Query("SELECT * FROM Section")
    fun getSections(): List<Section>

    @Query("SELECT * FROM Section")
    fun getSectionsSingle(): Single<List<Section>>

    @Query("SELECT * FROM Section")
    fun getSectionsObs(): Observable<List<Section>>

    // Update

    @Update
    fun update(section: Section): Int

    @Update
    fun update(sections: List<Section>): Int

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(section: Section): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sections: List<Section>): List<Long>

    // Delete

    @Delete
    fun delete(section: Section)

    @Delete
    fun delete(sections: List<Section>)

    @Query("DELETE FROM Section WHERE uid NOT IN (:map)")
    fun deleteAllNotInUids(map: List<Long>): Completable
}
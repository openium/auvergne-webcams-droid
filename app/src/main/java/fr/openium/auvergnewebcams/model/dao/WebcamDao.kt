package fr.openium.auvergnewebcams.model.dao

import androidx.room.*
import fr.openium.auvergnewebcams.model.entity.Webcam
import io.reactivex.Observable
import io.reactivex.Single


@Dao
abstract class WebcamDao {

    // Queries

    @Query("SELECT * FROM Webcam WHERE uid == :webcamId ORDER BY uid ASC LIMIT 1")
    abstract fun getWebcam(webcamId: Long): Webcam?

    @Query("SELECT * FROM Webcam WHERE uid == :webcamId ORDER BY uid ASC LIMIT 1")
    abstract fun getWebcamObs(webcamId: Long): Observable<List<Webcam>>

    @Query("SELECT * FROM Webcam")
    abstract fun getWebcams(): List<Webcam>

    @Query("SELECT * FROM Webcam")
    abstract fun getWebcamsSingle(): Single<List<Webcam>>

    @Query("SELECT * FROM Webcam")
    abstract fun getWebcamsObs(): Observable<List<Webcam>>

    @Query("SELECT * FROM Webcam WHERE sectionUid = :sectionId")
    abstract fun getWebcamsObs(sectionId: String): Observable<List<Webcam>>

    @Query("SELECT * FROM Webcam WHERE imageLD LIKE '%' || :url || '%' OR imageHD LIKE '%' || :url || '%' OR mediaViewSurfLD LIKE '%' || :url || '%' OR mediaViewSurfHD LIKE '%' || :url || '%' ORDER BY uid ASC LIMIT 1")
    abstract fun getWebcamWithPartialUrl(url: String): Webcam?

    // Updates

    @Update
    abstract fun update(webcam: Webcam): Int

    @Update
    abstract fun update(webcams: List<Webcam>): Int

    // Inserts

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(webcam: Webcam): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(webcams: List<Webcam>): List<Long>

    // Deletes

    @Delete
    abstract fun delete(webcam: Webcam)

    @Delete
    abstract fun delete(webcams: List<Webcam>)

    @Query("DELETE FROM Webcam WHERE (uid NOT IN (:map)) AND sectionUid == :sectionUid")
    abstract fun deleteAllNoMoreInSection(map: List<Long>, sectionUid: Long)
}
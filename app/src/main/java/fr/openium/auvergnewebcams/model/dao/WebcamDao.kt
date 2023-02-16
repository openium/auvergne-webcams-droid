package fr.openium.auvergnewebcams.model.dao

import androidx.room.*
import fr.openium.auvergnewebcams.model.entity.Webcam
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow


@Dao
interface WebcamDao {
    // Query
    @Query("SELECT * FROM Webcam")
    fun watchAllWebcams(): Flow<List<Webcam>>

    @Query("SELECT * FROM Webcam WHERE uid == :webcamId LIMIT 1")
    fun getWebcam(webcamId: Long): Webcam?

    @Query("SELECT * FROM Webcam WHERE uid == :webcamId LIMIT 1")
    fun getWebcamSingle(webcamId: Long): Single<Webcam?>

    @Query("SELECT * FROM Webcam WHERE uid == :webcamId LIMIT 1")
    suspend fun getWebcamForId(webcamId: Long?): Webcam?

    @Query("SELECT * FROM Webcam")
    fun getWebcamsSingle(): Single<List<Webcam>>

    @Query("SELECT * FROM Webcam WHERE sectionUid == :sectionId")
    fun getWebcamsSingle(sectionId: Long): Single<List<Webcam>>

    @Query("SELECT * FROM Webcam WHERE imageLD LIKE '%' || :url || '%' OR imageHD LIKE '%' || :url || '%' OR mediaViewSurfLD LIKE '%' || :url || '%' OR mediaViewSurfHD LIKE '%' || :url || '%' ORDER BY uid ASC LIMIT 1")
    fun getWebcamWithPartialUrl(url: String): Webcam?

    // Update

    @Update
    suspend fun update(webcam: Webcam): Int

    @Update
    fun update(webcams: List<Webcam>): Int

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(webcam: Webcam): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(webcams: List<Webcam>): List<Long>

    // Delete

    @Query("DELETE FROM Webcam WHERE (uid NOT IN (:map)) AND sectionUid == :sectionUid")
    fun deleteAllNoMoreInSection(map: List<Long>, sectionUid: Long)
}
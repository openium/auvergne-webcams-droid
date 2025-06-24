package fr.openium.auvergnewebcams.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.openium.auvergnewebcams.model.entity.Webcam
import kotlinx.coroutines.flow.Flow


@Dao
interface WebcamDao {
    // Query
    @Query("SELECT * FROM Webcam")
    fun watchAllWebcams(): Flow<List<Webcam>>

    @Query("SELECT * FROM Webcam WHERE uid == :webcamId LIMIT 1")
    suspend fun getWebcam(webcamId: Long): Webcam?

    @Query("SELECT * FROM Webcam WHERE uid == :webcamId LIMIT 1")
    fun getWebcamFlow(webcamId: Long): Flow<Webcam?>

    @Query("SELECT * FROM Webcam WHERE imageLD LIKE '%' || :url || '%' OR imageHD LIKE '%' || :url || '%' OR mediaViewSurfLD LIKE '%' || :url || '%' OR mediaViewSurfHD LIKE '%' || :url || '%' ORDER BY uid ASC LIMIT 1")
    suspend fun getWebcamWithPartialUrl(url: String): Webcam?

    // Update

    @Update
    suspend fun update(webcam: Webcam): Int

    @Update
    suspend fun update(webcams: List<Webcam>): Int

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(webcam: Webcam): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(webcams: List<Webcam>): List<Long>

    // Delete

    @Query("DELETE FROM Webcam WHERE (uid NOT IN (:map)) AND sectionUid == :sectionUid")
    suspend fun deleteAllNoMoreInSection(map: List<Long>, sectionUid: Long)
}
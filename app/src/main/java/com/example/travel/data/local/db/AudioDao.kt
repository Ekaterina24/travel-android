package com.example.travel.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudio(audio: AudioItem)

    @Query("SELECT * FROM audio_item WHERE placeId = :placeId")
    suspend fun observeAudioListByPlaceId(placeId: Long): List<AudioItem>

    @Query("SELECT * FROM audio_item WHERE id = :id")
    suspend fun observeAudioById(id: Long): AudioItem

    @Query("UPDATE audio_item SET status = :status WHERE id = :id")
    suspend fun updateStatus(status: String, id: Long)

}
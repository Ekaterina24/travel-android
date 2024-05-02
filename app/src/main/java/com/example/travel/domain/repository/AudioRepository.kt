package com.example.travel.domain.repository

import com.example.travel.data.local.db.AudioItem
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    suspend fun getAudioListByPlace(placeId: String): List<AudioModel>
    suspend fun getAudioList(): List<AudioModel>

    // Local
    suspend fun insertAudio(audio: AudioModel)
    suspend fun observeAudioListByPlaceId(placeId: Long): List<AudioModel>
    suspend fun observeAudioById(id: Long): AudioModel
    suspend fun updateStatus(status: String, id: Long)

}
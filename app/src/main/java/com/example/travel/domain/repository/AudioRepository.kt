package com.example.travel.domain.repository

import com.example.travel.domain.model.AudioModel

interface AudioRepository {
    suspend fun getAudioListByPlace(placeId: String): List<AudioModel>
    suspend fun getAudioList(): List<AudioModel>

}
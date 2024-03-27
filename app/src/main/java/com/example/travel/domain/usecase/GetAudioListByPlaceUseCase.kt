package com.example.travel.domain.usecase

import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.AudioRepository
import com.example.travel.domain.repository.CityRepository

class GetAudioListByPlaceUseCase(
    private val repository: AudioRepository
) {
    suspend operator fun invoke(placeId: String): List<AudioModel> {
        return repository.getAudioListByPlace(placeId)
    }
}
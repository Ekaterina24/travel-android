package com.example.travel.domain.usecase

import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.AudioRepository
import com.example.travel.domain.repository.CityRepository

class UpdateAudioUseCase(private val repo: AudioRepository) {
    suspend operator fun invoke(status: String, id: Long) =
        repo.updateStatus(status, id)
}
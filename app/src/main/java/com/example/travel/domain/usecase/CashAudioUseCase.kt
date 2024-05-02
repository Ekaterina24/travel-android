package com.example.travel.domain.usecase

import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.AudioRepository
import com.example.travel.domain.repository.CityRepository

class CashAudioUseCase(private val repo: AudioRepository) {
    suspend operator fun invoke(audio: AudioModel) =
        repo.insertAudio(audio)
}
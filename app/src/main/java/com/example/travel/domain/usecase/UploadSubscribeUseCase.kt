package com.example.travel.domain.usecase

import com.example.travel.domain.model.SubscribeModel
import com.example.travel.domain.repository.SubscribeRepository

class UploadSubscribeUseCase(private val repo: SubscribeRepository) {
    suspend operator fun invoke() =
        repo.observeSubscribeList()
}
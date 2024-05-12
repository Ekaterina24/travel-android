package com.example.travel.domain.usecase.subscribe

import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.repository.SubscribeRepository

class CreateSubscribeUseCase(
    private val repository: SubscribeRepository
) {
    suspend operator fun invoke(token: String, createSubscribeModel: CreateSubscribeModel) {
        repository.createSubscribe(token, createSubscribeModel)
    }
}
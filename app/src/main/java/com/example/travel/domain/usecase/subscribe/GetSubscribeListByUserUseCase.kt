package com.example.travel.domain.usecase.subscribe

import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.repository.SubscribeRepository

class GetSubscribeListByUserUseCase(
    private val repository: SubscribeRepository
) {
    suspend operator fun invoke(token: String): List<GetSubscribeModel> {
        return repository.getSubscribeListByUser(token)
    }
}
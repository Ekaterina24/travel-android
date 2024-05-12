package com.example.travel.domain.usecase

import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.repository.SubscribeRepository

class CashSubscribeListUseCase(private val repo: SubscribeRepository) {
    suspend operator fun invoke(subscribe: GetSubscribeModel) =
        repo.insertSubscribe(subscribe)
}
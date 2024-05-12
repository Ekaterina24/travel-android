package com.example.travel.domain.usecase.user

import com.example.travel.domain.model.UpdateScoresRequest
import com.example.travel.domain.repository.UserRepository

class UpdateScoresFromApiUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(token: String, scores: UpdateScoresRequest) =
        repo.updateScoresFromApi(token, scores)
}
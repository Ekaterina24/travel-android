package com.example.travel.domain.usecase.user

import com.example.travel.domain.model.UpdateEmailRequest
import com.example.travel.domain.repository.UserRepository

class UpdateEmailFromApiUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(token: String, email: UpdateEmailRequest) =
        repo.updateEmailFromApi(token, email)
}
package com.example.travel.domain.usecase.user

import com.example.travel.domain.model.UserProfileModel
import com.example.travel.domain.repository.UserRepository

class GetUserListUseCase(private val repo: UserRepository) {
    suspend operator fun invoke() : List<UserProfileModel> =
        repo.getUserList()
}
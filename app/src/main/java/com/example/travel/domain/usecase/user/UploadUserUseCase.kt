package com.example.travel.domain.usecase.user

import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.UserModel
import com.example.travel.domain.model.UserProfileModel
import com.example.travel.domain.repository.CityRepository
import com.example.travel.domain.repository.PlaceRepository
import com.example.travel.domain.repository.UserRepository

class UploadUserUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(): UserProfileModel =
        repo.observeUser()
}
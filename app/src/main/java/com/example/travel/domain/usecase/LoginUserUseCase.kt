package com.example.travel.domain.usecase

import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.repository.DayPlacesRepository
import com.example.travel.domain.repository.UserRepository

class LoginUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: LoginModel): TokenModel {
        return repository.loginUser(user)
    }
}
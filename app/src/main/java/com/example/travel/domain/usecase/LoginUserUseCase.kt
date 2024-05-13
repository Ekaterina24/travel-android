package com.example.travel.domain.usecase

import com.example.travel.domain.ApiResponse
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.repository.DayPlacesRepository
import com.example.travel.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class LoginUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: LoginModel): Flow<ApiResponse<TokenModel>> {
        return repository.loginUser(user)
    }
}
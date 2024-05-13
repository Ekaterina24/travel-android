package com.example.travel.domain.usecase

import com.example.travel.domain.ApiResponse
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.repository.DayPlacesRepository
import com.example.travel.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class RegisterUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: RegisterModel): Flow<ApiResponse<Unit>> {
        return repository.register(user)
    }
}
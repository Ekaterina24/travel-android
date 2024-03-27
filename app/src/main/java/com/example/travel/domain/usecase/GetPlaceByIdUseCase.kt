package com.example.travel.domain.usecase

import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository

class GetPlaceByIdUseCase(
    private val repository: PlaceRepository
) {
    suspend operator fun invoke(id: String): PlaceModel {
        return repository.getPlaceById(id)
    }
}
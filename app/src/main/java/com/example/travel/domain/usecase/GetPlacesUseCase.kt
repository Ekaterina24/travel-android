package com.example.travel.domain.usecase

import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository

class GetPlacesUseCase(
    private val repository: PlaceRepository
) {
    suspend operator fun invoke(cityId: Int, search: String, category: String): List<PlaceModel> {
        return repository.getPlaces(cityId, search, category)
    }
}
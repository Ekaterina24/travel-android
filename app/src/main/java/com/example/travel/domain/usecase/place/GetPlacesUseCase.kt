package com.example.travel.domain.usecase.place

import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository

class GetPlacesUseCase(
    private val repository: PlaceRepository
) {
    suspend operator fun invoke(cityId: Int, search: String, category: String): List<PlaceModel> {
        return repository.getPlaces(cityId, search, category)
    }

    suspend fun getData(cityId: Int, search: String, category: String): List<PlaceModel> {
        return repository.getData(cityId, search, category)
    }
}
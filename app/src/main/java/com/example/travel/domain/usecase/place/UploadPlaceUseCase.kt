package com.example.travel.domain.usecase.place

import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.CityRepository
import com.example.travel.domain.repository.PlaceRepository

class UploadPlaceUseCase(private val repo: PlaceRepository) {
    suspend operator fun invoke(generatedId: Long): PlaceModel =
        repo.observePlaceById(generatedId)
}
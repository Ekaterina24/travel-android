package com.example.travel.domain.usecase

import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.repository.DayPlacesRepository

class AddDayPlaceUseCase(
    private val repository: DayPlacesRepository
) {
    suspend operator fun invoke(token: String, dayPlacesModel: DayPlaceModel) {
        repository.addDayPlaces(token, dayPlacesModel)
    }
}
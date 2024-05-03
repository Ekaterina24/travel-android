package com.example.travel.domain.usecase.place

import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository

class CashPlaceUseCase(private val repo: PlaceRepository) {
    suspend operator fun invoke(placeModel: PlaceModel) =
        repo.insertPlaceItem(placeModel)
}
package com.example.travel.domain.usecase.place

import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository

class UpdatePlaceVisitedUseCase(private val repo: PlaceRepository) {
    suspend operator fun invoke(isVisited: Boolean, generatedId: Long) =
//    suspend operator fun invoke(placeModel: PlaceModel) =
        repo.updateIsVisited(isVisited, generatedId)
//        repo.updatePlace(placeModel)
}
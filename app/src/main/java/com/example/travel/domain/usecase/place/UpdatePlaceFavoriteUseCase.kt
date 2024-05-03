package com.example.travel.domain.usecase.place

import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository

class UpdatePlaceFavoriteUseCase(private val repo: PlaceRepository) {
    suspend operator fun invoke(isFavourite: Boolean, generatedId: Long) =
        repo.updateIsFavourite(isFavourite, generatedId)
}
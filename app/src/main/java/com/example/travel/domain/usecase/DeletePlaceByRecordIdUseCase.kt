package com.example.travel.domain.usecase

import com.example.travel.domain.repository.DayPlacesRepository

class DeletePlaceByRecordIdUseCase(
    private val repository: DayPlacesRepository
) {
    suspend operator fun invoke(token:String, placeId: String) {
        repository.deletePlaceByRecordId(token, placeId)
    }
}
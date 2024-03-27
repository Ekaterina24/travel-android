package com.example.travel.domain.usecase

import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.repository.PlaceRepository
import com.example.travel.domain.repository.TripRepository

class GetTripListByUserUseCase(
    private val repository: TripRepository
) {
    suspend operator fun invoke(token: String): List<GetTripListModel> {
        return repository.getTripListByUser(token)
    }
}
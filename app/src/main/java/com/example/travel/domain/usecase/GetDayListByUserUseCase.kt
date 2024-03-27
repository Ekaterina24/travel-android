package com.example.travel.domain.usecase

import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.repository.DayPlacesRepository

class GetDayListByUserUseCase(
    private val repository: DayPlacesRepository
) {
    suspend operator fun invoke(token: String, date: String): List<DayPlaceModel> {
       return repository.getPlaceListByUser(token, date)
    }
}
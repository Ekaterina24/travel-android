package com.example.travel.domain.usecase

import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.CityRepository

class GetCityListUseCase(
    private val repository: CityRepository
) {
    suspend operator fun invoke(): List<CityModel> {
        return repository.getCityList()
    }
}
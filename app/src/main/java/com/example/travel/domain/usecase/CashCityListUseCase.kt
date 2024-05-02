package com.example.travel.domain.usecase

import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.CityRepository

class CashCityListUseCase(private val repo: CityRepository) {
    suspend operator fun invoke(cityList: List<CityModel>) =
        repo.saveCityListToDb(cityList)
}
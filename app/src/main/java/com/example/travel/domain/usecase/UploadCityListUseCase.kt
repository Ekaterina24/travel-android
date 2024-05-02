package com.example.travel.domain.usecase

import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.CityRepository

class UploadCityListUseCase(private val repo: CityRepository) {
    suspend operator fun invoke(): List<CityModel> =
        repo.getCityListFromDb()
}
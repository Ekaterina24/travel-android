package com.example.travel.domain.usecase

import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.AudioRepository
import com.example.travel.domain.repository.CityRepository
import com.example.travel.domain.repository.PlaceRepository

class GetCategoryListUseCase(
    private val repository: PlaceRepository
) {
    suspend operator fun invoke(): List<CategoryModel> {
        return repository.getCategoryList()
    }
}
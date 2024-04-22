package com.example.travel.domain.repository

import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.PlaceModel

interface PlaceRepository {

    suspend fun getPlaces(cityId: Int, search: String, category: String): List<PlaceModel>
    suspend fun getCategoryList(): List<CategoryModel>
    suspend fun getPlaceById(id: String): PlaceModel

}
package com.example.travel.domain.repository

import com.example.travel.domain.model.PlaceModel

interface PlaceRepository {

    suspend fun getPlaces(cityId: Int): List<PlaceModel>

    suspend fun getPlaceById(id: String): PlaceModel

}
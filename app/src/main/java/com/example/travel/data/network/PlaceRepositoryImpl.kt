package com.example.travel.data.network

import com.example.travel.data.network.mapper.PlaceMapper
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository

class PlaceRepositoryImpl: PlaceRepository {
    private val mapper = PlaceMapper()

    override suspend fun getPlaces(cityId: Int): List<PlaceModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getPlaces(cityId))
    }
}
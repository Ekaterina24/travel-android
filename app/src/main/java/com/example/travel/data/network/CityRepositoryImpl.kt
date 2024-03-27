package com.example.travel.data.network

import com.example.travel.data.network.mapper.CityMapper
import com.example.travel.data.network.mapper.PlaceMapper
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.CityRepository
import com.example.travel.domain.repository.PlaceRepository

class CityRepositoryImpl: CityRepository {
    private val mapper = CityMapper()

    override suspend fun getCityList(): List<CityModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getCityList())
    }
}
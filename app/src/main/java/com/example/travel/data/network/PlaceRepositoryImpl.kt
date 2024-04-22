package com.example.travel.data.network

import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.network.mapper.PlaceMapper
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository
import retrofit2.http.Query

class PlaceRepositoryImpl: PlaceRepository {
    private val mapper = PlaceMapper()

    override suspend fun getPlaces(
        cityId: Int,
        search: String,
        category: String
    ): List<PlaceModel> {
        return mapper.mapListDtoToList(
            RetrofitInstance.travelApi.getPlaces(cityId, search, category))
    }

    override suspend fun getCategoryList(): List<CategoryModel> {
        return RetrofitInstance.travelApi.getCategoryList()
    }

    override suspend fun getPlaceById(id: String): PlaceModel {
        return mapper.mapDtoToModel(RetrofitInstance.travelApi.getPlaceById(id))
    }
}
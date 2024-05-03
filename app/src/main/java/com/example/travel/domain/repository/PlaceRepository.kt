package com.example.travel.domain.repository

import com.example.travel.data.local.db.PlaceItem
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import retrofit2.http.Query

interface PlaceRepository {

    suspend fun getPlaces(cityId: Int, search: String, category: String): List<PlaceModel>
    suspend fun getCategoryList(): List<CategoryModel>
    suspend fun getPlaceById(id: String): PlaceModel


    // Local
    suspend fun insertPlaceItem(placeModel: PlaceModel)
    suspend fun insertPlaceList(placeList: List<PlaceModel>)
    suspend fun observePlaceList(): List<PlaceModel>
    suspend fun observePlaceById(generatedId: Long): PlaceModel
    suspend fun updateIsVisited(isVisited: Boolean, generatedId: Long)
    suspend fun updateIsFavourite(isFavourite: Boolean, generatedId: Long)
    suspend fun updatePlace(placeModel: PlaceModel)

    suspend fun getData(cityId: Int, search: String, category: String): List<PlaceModel>


}
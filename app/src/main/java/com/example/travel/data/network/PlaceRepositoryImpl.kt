package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.mapper.PlaceMapper
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.PlaceRepository
import retrofit2.http.Query

class PlaceRepositoryImpl(
    application: Application
) : PlaceRepository {
    private val mapper = PlaceMapper()
    private val dao = TravelDatabase.getInstance(application).placeDao()

    override suspend fun getPlaces(
        cityId: Int,
        search: String,
        category: String
    ): List<PlaceModel> {
        return mapper.mapListDtoToList(
            RetrofitInstance.travelApi.getPlaces(cityId, search, category)
        )
    }

    override suspend fun getCategoryList(): List<CategoryModel> {
        return RetrofitInstance.travelApi.getCategoryList()
    }

    override suspend fun getPlaceById(id: String): PlaceModel {
        return mapper.mapDtoToModel(RetrofitInstance.travelApi.getPlaceById(id))
    }

    override suspend fun insertPlaceItem(placeModel: PlaceModel) {
        dao.insertPlaceItem(mapper.mapModelToDbModel(placeModel))
    }

    override suspend fun insertPlaceList(placeList: List<PlaceModel>) {
        dao.insertPlaceList(mapper.mapModelListToDbModelList(placeList))
    }

    override suspend fun observePlaceList(): List<PlaceModel> {
        return mapper.mapDbModelListToModelList(dao.observePlaceList())
    }

    override suspend fun observePlaceById(generatedId: Long): PlaceModel {
        return mapper.mapDbModelToModel(dao.observePlaceById(generatedId))
    }

    override suspend fun updateIsVisited(isVisited: Boolean, generatedId: Long) {
        dao.updateIsVisited(isVisited, generatedId)
    }

    override suspend fun updateIsFavourite(isFavourite: Boolean, generatedId: Long) {
        dao.updateIsFavourite(isFavourite, generatedId)
    }

    override suspend fun updatePlace(placeModel: PlaceModel) {
        dao.updatePlace(mapper.mapModelToDbModel(placeModel))
    }

    override suspend fun getData(cityId: Int, search: String, category: String): List<PlaceModel> {
        val cachedData = dao.observePlaceList()

        return if (cachedData.isEmpty()) {
            val newData = mapper.mapListDtoToList(RetrofitInstance.travelApi.getPlaces(cityId, search, category))
            dao.insertPlaceList(mapper.mapModelListToDbModelList(newData))
            newData
        } else {
            mapper.mapDbModelListToModelList(cachedData)
        }
    }
}
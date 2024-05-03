package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.local.db.CityDao
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.mapper.CityMapper
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.repository.CityRepository

class CityRepositoryImpl(
    application: Application
): CityRepository {
    private val mapper = CityMapper()
    private val dao = TravelDatabase.getInstance(application).cityDao()

    override suspend fun getCityList(): List<CityModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getCityList())
    }

    override suspend fun saveCityListToDb(cityList: List<CityModel>) {
       dao.insertCityList(mapper.mapModelListToDbModelList(cityList))
    }

    override suspend fun getCityFromDb(id: Int): CityModel {
//        return mapper.mapDbModelToModel(dao.)
        return CityModel(id = id, name = "name", latitude = 31.4, longitude = 23.4)
    }

    override suspend fun getCityListFromDb(): List<CityModel> {
        return mapper.mapDbModelListToModelList(dao.observeAllCityItems())
    }

    override suspend fun getData(): List<CityModel> {
        val cachedData = dao.observeAllCityItems()

        return if (cachedData.isEmpty()) {
            val newData = mapper.mapListDtoToList(RetrofitInstance.travelApi.getCityList())
            dao.insertCityList(mapper.mapModelListToDbModelList(newData))
            newData
        } else {
            mapper.mapDbModelListToModelList(cachedData)
        }
    }
}
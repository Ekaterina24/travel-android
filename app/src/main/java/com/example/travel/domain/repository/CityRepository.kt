package com.example.travel.domain.repository

import com.example.travel.domain.model.CityModel

interface CityRepository {
    // Network
    suspend fun getCityList(): List<CityModel>

    // Local
    suspend fun saveCityListToDb(cityList: List<CityModel>)
    suspend fun getCityFromDb(id: Int): CityModel
    suspend fun getCityListFromDb(): List<CityModel>
    suspend fun getData(): List<CityModel>

}
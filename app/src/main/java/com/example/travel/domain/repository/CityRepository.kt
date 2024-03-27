package com.example.travel.domain.repository

import com.example.travel.domain.model.CityModel

interface CityRepository {
    suspend fun getCityList(): List<CityModel>

}
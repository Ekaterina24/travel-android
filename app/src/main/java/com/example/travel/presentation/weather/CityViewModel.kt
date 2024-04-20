package com.example.travel.presentation.weather

import androidx.lifecycle.ViewModel
import com.example.travel.data.network.CityWeatherRepository
import com.example.travel.data.network.api.ApiClient
import com.example.travel.data.network.api.WeatherApi

class CityViewModel(val repository: CityWeatherRepository): ViewModel() {

    constructor():this(CityWeatherRepository(ApiClient().getClient().create(WeatherApi::class.java)))

    fun loadCityList(q: String, limit: Int) =
        repository.getCityList(q, limit)
}
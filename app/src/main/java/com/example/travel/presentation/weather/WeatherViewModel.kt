package com.example.travel.presentation.weather

import androidx.lifecycle.ViewModel
import com.example.travel.data.network.WeatherRepository
import com.example.travel.data.network.api.ApiClient
import com.example.travel.data.network.api.WeatherApi

class WeatherViewModel(val repository: WeatherRepository): ViewModel() {

    constructor():this(WeatherRepository(ApiClient().getClient().create(WeatherApi::class.java)))

    fun loadCurrentWeather(lat: Double, lon: Double, unit: String) =
        repository.getCurrentWeather(lat, lon, unit)

    fun loadForecastWeather(lat: Double, lon: Double, unit: String) =
        repository.getForecastWeather(lat, lon, unit)
}
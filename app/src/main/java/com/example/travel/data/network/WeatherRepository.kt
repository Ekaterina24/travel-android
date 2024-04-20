package com.example.travel.data.network

import com.example.travel.data.network.api.WeatherApi


class WeatherRepository(val api: WeatherApi) {

    fun getCurrentWeather(lat: Double, lon: Double, unit: String) =
        api.getCurrentWeather(lat, lon, unit, "2be1504527f8a926a31fae116f78bf17", "ru")

    fun getForecastWeather(lat: Double, lon: Double, unit: String) =
        api.getForecastWeather(lat, lon, unit, "2be1504527f8a926a31fae116f78bf17", "ru")
}
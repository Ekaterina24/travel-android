package com.example.travel.data.network

import com.example.travel.data.network.api.WeatherApi

class CityWeatherRepository(val api: WeatherApi) {

    fun getCityList(q: String, limit: Int) =
        api.getCityList(q, limit, "2be1504527f8a926a31fae116f78bf17")
}
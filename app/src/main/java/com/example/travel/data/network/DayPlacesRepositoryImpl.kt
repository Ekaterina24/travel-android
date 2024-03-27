package com.example.travel.data.network

import com.example.travel.data.network.mapper.DayPlaceMapper
import com.example.travel.data.network.mapper.TripMapper
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.repository.DayPlacesRepository

class DayPlacesRepositoryImpl: DayPlacesRepository {
    private val mapper = DayPlaceMapper()

    override suspend fun addDayPlaces(token: String, dayPlacesModel: DayPlaceModel) {
        RetrofitInstance.travelApi.addDayPlace(token, mapper.mapModelToDto(dayPlacesModel))
    }

    override suspend fun getPlaceListByUser(token: String): List<DayPlaceModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getPlaceListByUser(token))
    }


}
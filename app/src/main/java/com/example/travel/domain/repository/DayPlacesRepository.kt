package com.example.travel.domain.repository

import com.example.travel.domain.model.DayPlaceModel

interface DayPlacesRepository {

    suspend fun addDayPlaces(token: String, dayPlacesModel: DayPlaceModel)

    suspend fun getPlaceListByUser(token: String, date: String): List<DayPlaceModel>

    suspend fun deletePlaceByRecordId(token:String, placeId: String)
//    suspend fun updateDayPlaces()
//
//    suspend fun deleteDayPlaces()

}
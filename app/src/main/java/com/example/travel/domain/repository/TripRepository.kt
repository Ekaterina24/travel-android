package com.example.travel.domain.repository

import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TripModel

interface TripRepository {

    suspend fun createTrip(token: String, tripModel: TripModel)

    suspend fun getTripListByUser(token: String): List<GetTripListModel>

//    suspend fun updateTrip(tripModel: TripModel)

}
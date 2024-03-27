package com.example.travel.data.network

import com.example.travel.data.network.dto.TripDTO
import com.example.travel.data.network.mapper.PlaceMapper
import com.example.travel.data.network.mapper.TripMapper
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.repository.PlaceRepository
import com.example.travel.domain.repository.TripRepository

class TripRepositoryImpl: TripRepository {
    private val mapper = TripMapper()

    override suspend fun createTrip(token: String, tripModel: TripModel) {
        RetrofitInstance.travelApi.createTrip(token, mapper.mapModelToDto(tripModel))
    }

    override suspend fun getTripListByUser(token: String): List<GetTripListModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getTripListByUser(token))
    }


//    override suspend fun updateTrip(tripModel: TripModel) {
//        TODO("Not yet implemented")
//    }

}
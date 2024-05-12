package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.mapper.TripMapper
import com.example.travel.data.mapper.TypeSubMapper
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.model.TypeSubModel
import com.example.travel.domain.repository.TripRepository
import com.example.travel.domain.repository.TypeSubRepository

class TypeSubRepositoryImpl(
    application: Application
): TypeSubRepository {
    private val mapper = TypeSubMapper()
    override suspend fun getTypeSubList(): List<TypeSubModel> {
       return mapper.mapListDtoToList(RetrofitInstance.travelApi.getTypeSubList())
    }

}
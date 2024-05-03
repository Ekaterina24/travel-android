package com.example.travel.data.mapper

import com.example.travel.data.network.dto.DayPlaceDTO
import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.data.network.dto.TripDTO
import com.example.travel.data.network.dto.TripListDTO
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TripModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.Date

class DayPlaceMapper {

    private fun mapDtoToModel(dayPlaceDto: DayPlaceDTO) = DayPlaceModel(
        placeId = dayPlaceDto.placeId,
        dateVisiting = dayPlaceDto.dateVisiting,
        tripId = dayPlaceDto.tripId
    )

    fun mapModelToDto(dayPlaceModel: DayPlaceModel) = DayPlaceDTO(
        placeId = dayPlaceModel.placeId,
        dateVisiting = dayPlaceModel.dateVisiting,
        tripId = dayPlaceModel.tripId
    )

    fun mapListDtoToList(dtoList: List<DayPlaceDTO>): List<DayPlaceModel> {
        val resList = mutableListOf<DayPlaceModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }
}
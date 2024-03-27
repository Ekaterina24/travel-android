package com.example.travel.data.network.mapper

import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.data.network.dto.TripDTO
import com.example.travel.data.network.dto.TripListDTO
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.TripModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.Date

class TripMapper {

    private fun mapDtoToModel(tripDto: TripListDTO) = GetTripListModel(
        id = tripDto.id,
        date_start = tripDto.date_start,
        date_finish = tripDto.date_finish
    )

    fun mapModelToDto(tripModel: TripModel) = TripDTO(
        date_start = tripModel.date_start,
        date_finish = tripModel.date_finish
    )

    fun mapListDtoToList(dtoList: List<TripListDTO>): List<GetTripListModel> {
        val resList = mutableListOf<GetTripListModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }
}
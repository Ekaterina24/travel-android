package com.example.travel.data.network.mapper

import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.domain.model.PlaceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.Date

class PlaceMapper {

    fun mapDtoToModel(placeDto: PlaceDTO) = PlaceModel(
        generatedId = placeDto.generatedId,
        id = placeDto.id,
        name = placeDto.name,
        description = placeDto.description,
        addressId = placeDto.addressId,
        typePlace = placeDto.typePlace,
        subTypePlace = placeDto.subTypePlace,
        latitude = placeDto.latitude,
        longitude = placeDto.longitude,
        is_visited = placeDto.is_visited,
        is_favourite = placeDto.is_favourite,
        updated_at = placeDto.updated_at,
        cityId = placeDto.cityId
    )

    fun mapListDtoToList(dtoList: List<PlaceDTO>): List<PlaceModel> {
        val resList = mutableListOf<PlaceModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }
}
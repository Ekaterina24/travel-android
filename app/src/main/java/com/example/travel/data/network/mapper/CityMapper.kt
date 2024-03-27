package com.example.travel.data.network.mapper

import com.example.travel.data.network.dto.CityDTO
import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.Date

class CityMapper {

    private fun mapDtoToModel(cityDto: CityDTO) = CityModel(
        id = cityDto.generatedId,
        name = cityDto.name,
        latitude = cityDto.latitude,
        longitude = cityDto.longitude,
    )

    fun mapListDtoToList(dtoList: List<CityDTO>): List<CityModel> {
        val resList = mutableListOf<CityModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }
}
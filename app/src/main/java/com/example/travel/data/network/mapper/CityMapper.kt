package com.example.travel.data.network.mapper

import com.example.travel.data.local.db.CityItem
import com.example.travel.data.network.dto.CityDTO
import com.example.travel.domain.model.CityModel

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

    fun mapModelToDbModel(cityModel: CityModel) = CityItem(
        id = cityModel.id,
        name = cityModel.name,
        latitude = cityModel.latitude,
        longitude = cityModel.longitude
    )

    fun mapModelListToDbModelList(cityList: List<CityModel>) =
        cityList.map { mapModelToDbModel(it) }

    fun mapDbModelToModel(cityDb: CityItem) = CityModel(
        id = cityDb.id,
        name = cityDb.name,
        latitude = cityDb.latitude,
        longitude = cityDb.longitude
    )

    fun mapDbModelListToModelList(cityDbList: List<CityItem>) =
        cityDbList.map { mapDbModelToModel(it) }
}
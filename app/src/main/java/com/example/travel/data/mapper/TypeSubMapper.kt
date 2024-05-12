package com.example.travel.data.mapper

import com.example.travel.data.network.dto.TypeSubDTO
import com.example.travel.domain.model.TypeSubModel

class TypeSubMapper {

    private fun mapDtoToModel(typeSubDto: TypeSubDTO) = TypeSubModel(
        id = typeSubDto.id,
        period = typeSubDto.period,
        price = typeSubDto.price
    )

    fun mapModelToDto(typeSubModel: TypeSubModel) = TypeSubDTO(
        id = typeSubModel.id,
        period = typeSubModel.period,
        price = typeSubModel.price
    )

    fun mapListDtoToList(dtoList: List<TypeSubDTO>): List<TypeSubModel> {
        val resList = mutableListOf<TypeSubModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }
}
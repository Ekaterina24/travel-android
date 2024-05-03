package com.example.travel.data.mapper

import com.example.travel.data.local.db.PlaceItem
import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.domain.model.PlaceModel

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

    fun mapModelToDbModel(placeModel: PlaceModel) = PlaceItem(
        generatedId = placeModel.generatedId,
        id = placeModel.id,
        name = placeModel.name,
        description = placeModel.description,
        addressId = placeModel.addressId,
        typePlace = placeModel.typePlace,
        subTypePlace = placeModel.subTypePlace,
        latitude = placeModel.latitude,
        longitude = placeModel.longitude,
        isVisited = placeModel.is_visited,
        isFavourite = placeModel.is_favourite,
        updatedAt = placeModel.updated_at,
        cityId = placeModel.cityId
    )

    fun mapModelListToDbModelList(placeList: List<PlaceModel>) =
        placeList.map { mapModelToDbModel(it) }

    fun mapDbModelToModel(placeDb: PlaceItem) = PlaceModel(
        generatedId = placeDb.generatedId,
        id = placeDb.id,
        name = placeDb.name,
        description = placeDb.description,
        addressId = placeDb.addressId,
        typePlace = placeDb.typePlace,
        subTypePlace = placeDb.subTypePlace,
        latitude = placeDb.latitude,
        longitude = placeDb.longitude,
        is_visited = placeDb.isVisited,
        is_favourite = placeDb.isFavourite,
        updated_at = placeDb.updatedAt,
        cityId = placeDb.cityId
    )

    fun mapDbModelListToModelList(placeDbList: List<PlaceItem>) =
        placeDbList.map { mapDbModelToModel(it) }
}
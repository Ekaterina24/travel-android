package com.example.travel.data.network.mapper

import com.example.travel.data.local.db.SubscribeItem
import com.example.travel.domain.model.SubscribeModel

class SubscribeMapper {

//    private fun mapDtoToModel(audioDto: AudioDTO) = AudioModel(
//        id = audioDto.generatedId,
//        placeId = audioDto.placeId,
//        text = audioDto.text,
//        status = audioDto.status
//    )
//
//    fun mapListDtoToList(dtoList: List<AudioDTO>): List<AudioModel> {
//        val resList = mutableListOf<AudioModel>()
//        dtoList.forEach { resList.add(mapDtoToModel(it)) }
//        return resList
//    }
//
//
    fun mapModelToDbModel(subscribeModel: SubscribeModel) = SubscribeItem(
        id = subscribeModel.id,
        userId = subscribeModel.userId,
        typeId = subscribeModel.typeId,
        date = subscribeModel.date
    )

    fun mapModelListToDbModelList(subscribeList: List<SubscribeModel>) =
        subscribeList.map { mapModelToDbModel(it) }

    fun mapDbModelToModel(subscribeDb: SubscribeItem) = SubscribeModel(
        id = subscribeDb.id,
        userId = subscribeDb.userId,
        typeId = subscribeDb.typeId,
        date = subscribeDb.date
    )

    fun mapDbModelListToModelList(subscribeDbList: List<SubscribeItem>) =
        subscribeDbList.map { mapDbModelToModel(it) }
}
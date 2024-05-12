package com.example.travel.data.mapper

import com.example.travel.data.network.dto.CreateSubscribeDTO
import com.example.travel.data.network.dto.GetSubscribeDTO
import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.GetSubscribeModel

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

    // Network
    private fun mapDtoToModel(createSubscribeDto: CreateSubscribeDTO) = CreateSubscribeModel(
//        id = subscribeDto.generatedId,
        typeId = createSubscribeDto.typeId,
        city = createSubscribeDto.city,
    )

    fun mapModelToDto(createSubscribeModel: CreateSubscribeModel) = CreateSubscribeDTO(
//        generatedId = subscribeModel.id,
        typeId = createSubscribeModel.typeId,
        city = createSubscribeModel.city,
    )

    fun mapCreateListDtoToList(dtoList: List<CreateSubscribeDTO>): List<CreateSubscribeModel> {
        val resList = mutableListOf<CreateSubscribeModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }

    private fun mapDtoToModel(getSubscribeDto: GetSubscribeDTO) = GetSubscribeModel(
        id = getSubscribeDto.id,
        typeId = getSubscribeDto.typeId,
        city = getSubscribeDto.city,
        date = getSubscribeDto.date
    )

    fun mapModelToDto(getSubscribeModel: GetSubscribeModel) = GetSubscribeDTO(
        id = getSubscribeModel.id,
        typeId = getSubscribeModel.typeId,
        city = getSubscribeModel.city,
        date = getSubscribeModel.date
    )

    fun mapGetListDtoToList(dtoList: List<GetSubscribeDTO>): List<GetSubscribeModel> {
        val resList = mutableListOf<GetSubscribeModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }

    // Local
//    fun mapModelToDbModel(subscribeModel: SubscribeModel) = SubscribeItem(
////        id = subscribeModel.id,
////        userId = subscribeModel.userId,
//        city = subscribeModel.city,
//        typeId = subscribeModel.typeId,
////        date = subscribeModel.date
//    )
//
//    fun mapModelListToDbModelList(subscribeList: List<SubscribeModel>) =
//        subscribeList.map { mapModelToDbModel(it) }
//
//    fun mapDbModelToModel(subscribeDb: SubscribeItem) = SubscribeModel(
////        id = subscribeDb.id,
////        userId = subscribeDb.userId,
//        typeId = subscribeDb.typeId,
//        city = subscribeDb.city,
////        date = subscribeDb.date
//    )
//
//    fun mapDbModelListToModelList(subscribeDbList: List<SubscribeItem>) =
//        subscribeDbList.map { mapDbModelToModel(it) }
}
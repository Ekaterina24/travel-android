package com.example.travel.data.network.mapper

import com.example.travel.data.local.db.AudioItem
import com.example.travel.data.network.dto.AudioDTO
import com.example.travel.domain.model.AudioModel

class AudioMapper {

    private fun mapDtoToModel(audioDto: AudioDTO) = AudioModel(
        id = audioDto.generatedId,
        placeId = audioDto.placeId,
        text = audioDto.desc,
        status = audioDto.status
    )

    fun mapListDtoToList(dtoList: List<AudioDTO>): List<AudioModel> {
        val resList = mutableListOf<AudioModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }


    fun mapModelToDbModel(audioModel: AudioModel) = AudioItem(
        id = audioModel.id,
        placeId = audioModel.placeId,
        text = audioModel.text,
        status = audioModel.status
    )

    fun mapModelListToDbModelList(audioList: List<AudioModel>) =
        audioList.map { mapModelToDbModel(it) }

    fun mapDbModelToModel(audioDb: AudioItem) = AudioModel(
        id = audioDb.id,
        placeId = audioDb.placeId,
        text = audioDb.text,
        status = audioDb.status
    )

    fun mapDbModelListToModelList(audioDbList: List<AudioItem>) =
        audioDbList.map { mapDbModelToModel(it) }
}
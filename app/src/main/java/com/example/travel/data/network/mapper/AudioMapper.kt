package com.example.travel.data.network.mapper

import com.example.travel.data.network.dto.AudioDTO
import com.example.travel.data.network.dto.CityDTO
import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.Date

class AudioMapper {

    private fun mapDtoToModel(audioDto: AudioDTO) = AudioModel(
        name = audioDto.name,
        desc = audioDto.desc,
        status = audioDto.status,
        placeId = audioDto.placeId
    )

    fun mapListDtoToList(dtoList: List<AudioDTO>): List<AudioModel> {
        val resList = mutableListOf<AudioModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }
}
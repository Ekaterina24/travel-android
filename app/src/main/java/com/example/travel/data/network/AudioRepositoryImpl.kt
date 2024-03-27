package com.example.travel.data.network

import com.example.travel.data.network.mapper.AudioMapper
import com.example.travel.data.network.mapper.CityMapper
import com.example.travel.data.network.mapper.PlaceMapper
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.repository.AudioRepository
import com.example.travel.domain.repository.CityRepository
import com.example.travel.domain.repository.PlaceRepository

class AudioRepositoryImpl: AudioRepository {
    private val mapper = AudioMapper()

    override suspend fun getAudioListByPlace(placeId: String): List<AudioModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getAudioListByPlace(placeId))
    }

    override suspend fun getAudioList(): List<AudioModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getAudioList())
    }
}
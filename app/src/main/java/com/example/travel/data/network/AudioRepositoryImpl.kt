package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.network.mapper.AudioMapper
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.repository.AudioRepository

class AudioRepositoryImpl(
    application: Application
): AudioRepository {
    private val mapper = AudioMapper()
    private val dao = TravelDatabase.getInstance(application).audioDao()

    override suspend fun getAudioListByPlace(placeId: String): List<AudioModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getAudioListByPlace(placeId))
    }

    override suspend fun getAudioList(): List<AudioModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getAudioList())
    }

    override suspend fun insertAudio(audio: AudioModel) {
        dao.insertAudio(mapper.mapModelToDbModel(audio))
    }

    override suspend fun observeAudioListByPlaceId(placeId: Long): List<AudioModel> {
        return mapper.mapDbModelListToModelList(dao.observeAudioListByPlaceId(placeId))
    }

    override suspend fun observeAudioById(id: Long): AudioModel {
        return mapper.mapDbModelToModel(dao.observeAudioById(id))
    }

    override suspend fun updateStatus(status: String, id: Long) {
        dao.updateStatus(status, id)
    }
}
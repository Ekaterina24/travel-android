package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.network.mapper.AudioMapper
import com.example.travel.data.network.mapper.SubscribeMapper
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.SubscribeModel
import com.example.travel.domain.repository.AudioRepository
import com.example.travel.domain.repository.SubscribeRepository

class SubscribeRepositoryImpl(
    application: Application
): SubscribeRepository {
    private val mapper = SubscribeMapper()
    private val dao = TravelDatabase.getInstance(application).subscribeDao()
    override suspend fun insertSubscribe(subscribe: SubscribeModel) {
        dao.insertSubscribe(mapper.mapModelToDbModel(subscribe))
    }

    override suspend fun insertSubscribeList(subscribeList: List<SubscribeModel>) {
        dao.insertSubscribeList(mapper.mapModelListToDbModelList(subscribeList))
    }

    override fun observeSubscribeList(): List<SubscribeModel> {
        return mapper.mapDbModelListToModelList(dao.observeSubscribeList())
    }


}
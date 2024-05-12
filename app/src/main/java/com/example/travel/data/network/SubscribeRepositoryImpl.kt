package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.mapper.SubscribeMapper
import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.repository.SubscribeRepository

class SubscribeRepositoryImpl(
    application: Application
): SubscribeRepository {
    private val mapper = SubscribeMapper()
    private val dao = TravelDatabase.getInstance(application).subscribeDao()
    override suspend fun createSubscribe(token: String, createSubscribeModel: CreateSubscribeModel) {
        RetrofitInstance.travelApi.createSubscribe(token, mapper.mapModelToDto(createSubscribeModel))
    }

    override suspend fun getSubscribeListByUser(token: String): List<GetSubscribeModel> {
        return mapper.mapGetListDtoToList(RetrofitInstance.travelApi.getSubscribeListByUser(token))
    }

    override suspend fun insertSubscribe(subscribe: GetSubscribeModel) {
//        dao.insertSubscribe(mapper.mapModelToDbModel(subscribe))
    }

    override suspend fun insertSubscribeList(subscribeList: List<GetSubscribeModel>) {
//        dao.insertSubscribeList(mapper.mapModelListToDbModelList(subscribeList))
    }

//    override fun observeSubscribeList(): Flow<List<SubscribeModel>> {
////        return mapper.mapDbModelListToModelList(dao.observeSubscribeList())
//    return dao.observeSubscribeList().map { dbSubscribeList -> mapper.mapDbModelListToModelList(dbSubscribeList) }
//}

    override suspend fun observeSubscribeList(): List<GetSubscribeModel> {
        return listOf()
//        return mapper.mapDbModelListToModelList(dao.observeSubscribeList())
//        return dao.observeSubscribeList().map { dbSubscribeList -> mapper.mapDbModelListToModelList(dbSubscribeList) }
    }


}
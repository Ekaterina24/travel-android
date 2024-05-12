package com.example.travel.domain.repository

import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.GetSubscribeModel

interface SubscribeRepository {

    // Network
    suspend fun createSubscribe(token: String, createSubscribeModel: CreateSubscribeModel)
    suspend fun getSubscribeListByUser(token: String): List<GetSubscribeModel>

    // Local
    suspend fun insertSubscribe(subscribe: GetSubscribeModel)
    suspend fun insertSubscribeList(subscribeList: List<GetSubscribeModel>)
    suspend fun observeSubscribeList(): List<GetSubscribeModel>
//    fun observeSubscribeList(): Flow<List<SubscribeModel>>
}
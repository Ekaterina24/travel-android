package com.example.travel.domain.repository

import com.example.travel.data.local.db.SubscribeItem
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.SubscribeModel
import kotlinx.coroutines.flow.Flow

interface SubscribeRepository {

    suspend fun insertSubscribe(subscribe: SubscribeModel)
    suspend fun insertSubscribeList(subscribeList: List<SubscribeModel>)
    suspend fun observeSubscribeList(): List<SubscribeModel>
//    fun observeSubscribeList(): Flow<List<SubscribeModel>>
}
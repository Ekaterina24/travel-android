package com.example.travel.domain.repository

import com.example.travel.domain.model.TypeSubModel

interface TypeSubRepository {

    suspend fun getTypeSubList(): List<TypeSubModel>

}
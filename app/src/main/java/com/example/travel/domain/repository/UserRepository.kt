package com.example.travel.domain.repository

import com.example.travel.domain.model.PlaceModel

interface UserRepository {

    suspend fun registerUser()

    suspend fun loginUser()

    suspend fun updateUser()

}
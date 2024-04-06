package com.example.travel.domain.repository

import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel

interface UserRepository {

    suspend fun register(user: RegisterModel)

    suspend fun loginUser(user: LoginModel): TokenModel

    suspend fun updateUser()

}
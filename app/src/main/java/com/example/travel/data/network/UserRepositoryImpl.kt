package com.example.travel.data.network

import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.network.mapper.UserMapper
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.repository.UserRepository

class UserRepositoryImpl: UserRepository {
    private val mapper = UserMapper()
    override suspend fun register(user: RegisterModel) {
        RetrofitInstance.travelApi.register(mapper.mapModelToDto(user))
    }

    override suspend fun loginUser(user: LoginModel): TokenModel {
        val res = mapper.mapDtoToModel(RetrofitInstance.travelApi.login(mapper.mapModelToDto(user)))

        return res
    }

    override suspend fun updateUser() {
//        TODO("Not yet implemented")
    }


}
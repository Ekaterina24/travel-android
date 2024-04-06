package com.example.travel.data.network

import com.example.travel.data.network.dto.TripDTO
import com.example.travel.data.network.mapper.PlaceMapper
import com.example.travel.data.network.mapper.TripMapper
import com.example.travel.data.network.mapper.UserMapper
import com.example.travel.domain.model.GetTripListModel
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.model.TripModel
import com.example.travel.domain.repository.PlaceRepository
import com.example.travel.domain.repository.TripRepository
import com.example.travel.domain.repository.UserRepository

class UserRepositoryImpl: UserRepository {
    private val mapper = UserMapper()
    override suspend fun register(user: RegisterModel) {
        RetrofitInstance.travelApi.register(mapper.mapModelToDto(user))
    }

    override suspend fun loginUser(user: LoginModel): TokenModel {
        return mapper.mapDtoToModel(RetrofitInstance.travelApi.login(mapper.mapModelToDto(user)))
    }

    override suspend fun updateUser() {
//        TODO("Not yet implemented")
    }


}
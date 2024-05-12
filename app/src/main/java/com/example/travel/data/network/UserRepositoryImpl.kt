package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.mapper.UserMapper
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.model.UpdateScoresRequest
import com.example.travel.domain.model.UserModel
import com.example.travel.domain.model.UserProfileModel
import com.example.travel.domain.repository.UserRepository

class UserRepositoryImpl(
    application: Application
): UserRepository {
    private val mapper = UserMapper()
    private val dao = TravelDatabase.getInstance(application).userDao()
    override suspend fun register(user: RegisterModel) {
        RetrofitInstance.travelApi.register(mapper.mapModelToDto(user))
    }

    override suspend fun loginUser(user: LoginModel): TokenModel {
        val res = mapper.mapDtoToModel(RetrofitInstance.travelApi.login(mapper.mapModelToDto(user)))

        return res
    }

    override suspend fun updateUser() {
//        RetrofitInstance.travelApi.
    }

    override suspend fun getUserProfile(token: String): UserProfileModel {
       return mapper.mapDtoToModel(RetrofitInstance.travelApi.getUserProfile(token))
    }

    override suspend fun getUserList(): List<UserProfileModel> {
        return mapper.mapListDtoToList(RetrofitInstance.travelApi.getUserList())
    }

    override suspend fun updateScoresFromApi(token: String, scores: UpdateScoresRequest) {
        RetrofitInstance.travelApi.updateScoresFromApi(token, scores)
    }

    override suspend fun insertUser(user: UserProfileModel) {
        dao.insertUser(mapper.mapModelToDbModel(user))
    }

    override suspend fun observeUser(): UserProfileModel {
        return mapper.mapDbModelToModel(dao.observeUser())
    }

    override suspend fun updateName(name: String, id: Long) {
        dao.updateName(name, id)
    }

    override suspend fun updateEmail(email: String, id: Long) {
        dao.updateEmail(email, id)
    }

    override suspend fun updateScores(scores: Long, id: Long) {
        dao.updateScores(scores, id)
    }

    override suspend fun getData(): List<UserModel> {
        TODO("Not yet implemented")
    }

//    override suspend fun getData(): List<UserModel> {
//        val cachedData = dao.observeUserList()
//
//        return if (cachedData.isEmpty()) {
//            val newData = mapper.map(RetrofitInstance.travelApi.getCityList())
//            dao.insertCityList(mapper.mapModelListToDbModelList(newData))
//            newData
//        } else {
//            mapper.mapDbModelListToModelList(cachedData)
//        }
//    }

}
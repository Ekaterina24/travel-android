package com.example.travel.domain.repository

import androidx.room.Query
import com.example.travel.data.local.db.UserItem
import com.example.travel.data.network.dto.UserDTO
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.model.UserModel
import com.example.travel.domain.model.UserProfileModel
import retrofit2.http.Header

interface UserRepository {

    suspend fun register(user: RegisterModel)
    suspend fun loginUser(user: LoginModel): TokenModel
    suspend fun updateUser()
    suspend fun getUserProfile(token: String): UserProfileModel

    // Local
    suspend fun insertUser(user: UserProfileModel)

    suspend fun observeUser(): UserProfileModel

    suspend fun updateName(name: String, id: Long)

    suspend fun updateEmail(email: String, id: Long)

    suspend fun updateScores(scores: Long, id: Long)

    suspend fun getData(): List<UserModel>


}
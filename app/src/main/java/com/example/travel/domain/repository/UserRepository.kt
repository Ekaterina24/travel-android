package com.example.travel.domain.repository

import com.example.travel.domain.ApiResponse
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.model.UpdateEmailRequest
import com.example.travel.domain.model.UpdateScoresRequest
import com.example.travel.domain.model.UserModel
import com.example.travel.domain.model.UserProfileModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun register(user: RegisterModel): Flow<ApiResponse<Unit>>
    suspend fun loginUser(user: LoginModel): Flow<ApiResponse<TokenModel>>
    suspend fun updateUser()
    suspend fun getUserProfile(token: String): UserProfileModel
    suspend fun getUserList(): List<UserProfileModel>
    suspend fun updateScoresFromApi(token: String, scores: UpdateScoresRequest)
    suspend fun updateEmailFromApi(token: String, email: UpdateEmailRequest)

    // Local
    suspend fun insertUser(user: UserProfileModel)
    suspend fun observeUser(): UserProfileModel
    suspend fun updateName(name: String, id: Long)
    suspend fun updateEmail(email: String, id: Long)
    suspend fun updateScores(scores: Long, id: Long)
    suspend fun getData(): List<UserModel>


}
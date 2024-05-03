package com.example.travel.domain.model

data class UserModel(
    val username: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val scores: Long
)

data class UserProfileModel(
    val id: Long,
    val username: String,
    val email: String,
    val scores: Long
)

enum class UserRole {
    MODERATOR,
    USER
}
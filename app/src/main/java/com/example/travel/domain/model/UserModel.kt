package com.example.travel.domain.model

data class UserModel(
    val id: Long,
    val username: String,
    val email: String,
    val password: String,
    val salt: String,
    val role: UserRole,
    val scores: Long
)

enum class UserRole {
    MODERATOR,
    USER
}
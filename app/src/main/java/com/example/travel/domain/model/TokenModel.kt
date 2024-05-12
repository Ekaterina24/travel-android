package com.example.travel.domain.model

data class TokenModel(
    val accessToken: String,
    val expiresIn: Long,
)

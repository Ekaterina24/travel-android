package com.example.travel.data.network.dto

data class CreateReviewDTO(
    val placeId: String,
    val text: String,
    val rating: Int
)
package com.example.travel.data.network.dto

import java.util.Date

data class GetReviewDTO(
    val id: Long,
    val userId: Long,
    val placeId: String,
    val text: String,
    val rating: Int,
    val date: Date
)
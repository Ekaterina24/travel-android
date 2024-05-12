package com.example.travel.domain.model.review

import java.util.Date

data class GetReviewModel(
    val id: Long,
    val userId: Long,
    val placeId: String,
    val text: String,
    val rating: Int,
    val date: Date
)
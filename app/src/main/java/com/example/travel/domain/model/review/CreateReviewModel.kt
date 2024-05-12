package com.example.travel.domain.model.review

data class CreateReviewModel(
    val placeId: String,
    val text: String,
    val rating: Int
)
package com.example.travel.domain.model.review

import java.util.Date

data class ReviewAdapterModel(
    val userName: String,
    val text: String,
    val rating: Int,
    val date: Date
)
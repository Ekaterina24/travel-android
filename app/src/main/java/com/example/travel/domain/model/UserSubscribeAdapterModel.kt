package com.example.travel.domain.model

import java.util.Date

data class UserSubscribeAdapterModel(
    val id: Long,
    val date_start: Date,
    val period: Int,
    val city: String
)


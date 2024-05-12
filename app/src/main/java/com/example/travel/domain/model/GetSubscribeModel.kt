package com.example.travel.domain.model

import java.util.Date

data class GetSubscribeModel(
    val id: Long,
    val typeId: Long,
    val city: String,
    val date: Date
)




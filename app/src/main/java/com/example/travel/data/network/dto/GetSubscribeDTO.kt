package com.example.travel.data.network.dto

import java.util.Date

data class GetSubscribeDTO(
    val id: Long,
    val typeId: Long,
    val city: String,
    val date: Date
)
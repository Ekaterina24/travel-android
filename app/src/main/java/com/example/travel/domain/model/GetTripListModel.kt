package com.example.travel.domain.model

data class GetTripListModel(
    val id: Int,
    val date_start: String,
    val date_finish: String,
    val city: String
    )
package com.example.travel.data.network.dto

import java.util.Date

data class PlaceDTO(
    val generatedId: Long,
    val id: String,
    val name: String,
    val description: String,
    val addressId: String,
    val typePlace: String,
    val subTypePlace: String,
    val latitude: String,
    val longitude: String,
    val is_visited: String,
    val is_favourite: String,
    val updated_at: Date,
    val cityId: Int
)
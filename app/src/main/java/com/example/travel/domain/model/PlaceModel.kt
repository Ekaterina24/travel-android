package com.example.travel.domain.model

import java.util.Date

data class PlaceModel(
    val generatedId: Long,
    val id: String,
    val name: String,
    val description: String,
    val addressId: String,
    val typePlace: String,
    val subTypePlace: String,
    val latitude: String,
    val longitude: String,
    val is_visited: Boolean,
    val is_favourite: Boolean,
    val updated_at: String,
    val cityId: Int
    )
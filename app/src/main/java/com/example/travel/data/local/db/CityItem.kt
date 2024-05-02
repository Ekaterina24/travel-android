package com.example.travel.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_item")
data class CityItem(
    @PrimaryKey
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

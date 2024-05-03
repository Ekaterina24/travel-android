package com.example.travel.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "place_item")
data class PlaceItem(
    @PrimaryKey
    val generatedId: Long,
    val id: String,
    val name: String,
    val description: String,
    val addressId: String,
    val typePlace: String,
    val subTypePlace: String,
    val latitude: String,
    val longitude: String,
    @SerializedName("is_visited")
    val isVisited: Boolean,
    @SerializedName("is_favourite")
    val isFavourite: Boolean,
    @SerializedName("updated_at")
    val updatedAt: String,
    val cityId: Int
)

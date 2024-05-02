package com.example.travel.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "audio_item")
data class AudioItem(
    @PrimaryKey
    val id: Int,
    @SerializedName("place_id")
    val placeId: String,
    val text: String,
    val status: String,
)

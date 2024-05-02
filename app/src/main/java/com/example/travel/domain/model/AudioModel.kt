package com.example.travel.domain.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AudioModel(
    val id: Int,
    val placeId: String,
    val text: String,
    val status: String,
)
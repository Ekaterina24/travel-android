package com.example.travel.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateSubscribeModel(
    val typeId: Long,
    val city: String,
)




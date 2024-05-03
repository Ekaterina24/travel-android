package com.example.travel.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.travel.domain.model.UserRole
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class UserItem(
    @PrimaryKey
    val id: Long,
    val name: String,
    val email: String,
    val scores: Long
)


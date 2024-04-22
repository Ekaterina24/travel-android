package com.example.travel.domain.model

import com.google.gson.annotations.SerializedName

data class CategoryModel (
    @SerializedName("place_typePlace")
    val category: String
)
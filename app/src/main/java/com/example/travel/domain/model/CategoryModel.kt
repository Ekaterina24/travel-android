package com.example.travel.domain.model

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

data class CategoryModel (
    @SerializedName("place_typePlace")
    val category: String,
    val img: Int
)
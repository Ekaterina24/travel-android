package com.example.travel.presentation.places

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class LocationModel(
    val geoPointsList: ArrayList<GeoPoint>
) : Serializable

package com.example.travel.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityItem(cityItem: CityItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityList(cityList: List<CityItem>)

    @Query("SELECT * FROM city_item")
    fun observeAllCityItems(): List<CityItem>

//    @Delete
//    suspend fun deleteCityItem(cityItem: CityItem)
}
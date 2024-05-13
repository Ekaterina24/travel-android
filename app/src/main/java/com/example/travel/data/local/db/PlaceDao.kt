package com.example.travel.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceItem(placeItem: PlaceItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceList(placeList: List<PlaceItem>)

    @Query("SELECT * FROM place_item")
    suspend fun observePlaceList(): List<PlaceItem>

    @Query("SELECT * FROM place_item WHERE generatedId = :generatedId")
    suspend fun observePlaceById(generatedId: Long): PlaceItem

    @Query("SELECT * FROM place_item WHERE id = :id")
    suspend fun observePlaceByIdFromApi(id: String): PlaceItem

    @Query("UPDATE place_item SET isVisited = :isVisited WHERE generatedId = :generatedId")
    suspend fun updateIsVisited(isVisited: Boolean, generatedId: Long)

    @Query("UPDATE place_item SET " +
            "isFavourite = :isFavourite WHERE generatedId = :generatedId")
    suspend fun updateIsFavourite(isFavourite: Boolean, generatedId: Long)

    @Update
    suspend fun updatePlace(placeItem: PlaceItem)

}
package com.example.travel.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscribeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscribe(subscribe: SubscribeItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscribeList(subscribeList: List<SubscribeItem>)

    @Query("SELECT * FROM subscribe_item")
    fun observeSubscribeList(): List<SubscribeItem>

}
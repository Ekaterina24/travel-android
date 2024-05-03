package com.example.travel.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserItem)

    // несколько учетных записей
    @Query("SELECT * FROM user")
    suspend fun observeUserList(): List<UserItem>

//    @Query("SELECT * FROM user WHERE id = :id")
//    suspend fun observeUserById(id: Long): UserItem
@Query("SELECT * FROM user")
suspend fun observeUser(): UserItem

    @Query("UPDATE user SET name = :name WHERE id = :id")
    suspend fun updateName(name: String, id: Long)

    @Query("UPDATE user SET email = :email WHERE id = :id")
    suspend fun updateEmail(email: String, id: Long)

    @Query("UPDATE user SET scores = :scores WHERE id = :id")
    suspend fun updateScores(scores: Long, id: Long)

//    @Update
//    suspend fun updatePlace(placeItem: PlaceItem)

}
package com.example.travel.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "subscribe_item")
data class SubscribeItem(
    @PrimaryKey
    val id: Long,
//    @SerializedName("user_id")
//    val userId: Long,
    @SerializedName("type_id")
//    val typeId: String,
    val typeId: Int,
    val date: Date,
    val city: String
)


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

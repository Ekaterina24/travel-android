package com.example.travel.data.local.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CityItem::class,
//        PlaceItem::class,
//        AddressItem::class,
//        UserItem::class,
//        TripItem::class,
//        DayPlacesItem::class,
//        AudioItem::class,
//        ReviewItem::class,
//        SubscribeItem::class,
//        TypeSubscribeItem::class
    ],
    version = 1,
    exportSchema = false
)
//@TypeConverters(Converters::class)
abstract class TravelDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao
//    abstract fun placeDao(): PlaceDao
//    abstract fun addressDao(): AddressDao
//    abstract fun userDao(): UserDao
//    abstract fun tripDao(): TripDao
//    abstract fun placeOnDayDao(): DayPlacesDao
//    abstract fun audioDao(): AudioDao
//    abstract fun reviewDao(): ReviewDao
//    abstract fun subscribeDao(): SubscribeDao
//    abstract fun typeSubscribeDao(): TypeSubscribeDao

    companion object {

        private var INSTANCE: TravelDatabase? = null
        private const val DB_NAME = "app.db"
        private val LOCK = Any()

        fun getInstance(application: Application): TravelDatabase {

            INSTANCE?.let { db ->
                return db
            } // check 1

            synchronized(LOCK) {
                INSTANCE?.let { db ->
                    return db
                } // check 2

                val db = Room.databaseBuilder(
                    application,
                    TravelDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}
package com.example.apptravelfood.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.apptravelfood.data.local.dao.CheckinDao
import com.example.apptravelfood.data.local.dao.FoodItemDao
import com.example.apptravelfood.data.local.dao.FoodStoreDao
import com.example.apptravelfood.data.local.dao.FoodStoreReviewDao
import com.example.apptravelfood.data.local.dao.PlaceDao
import com.example.apptravelfood.data.local.dao.PointHistoryDao
import com.example.apptravelfood.data.local.dao.UserDao
import com.example.apptravelfood.data.local.entity.CheckinEntity
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.local.entity.FoodStoreReviewEntity
import com.example.apptravelfood.data.local.entity.PlaceEntity
import com.example.apptravelfood.data.local.entity.PointHistoryEntity
import com.example.apptravelfood.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PlaceEntity::class,
        FoodStoreEntity::class,
        FoodItemEntity::class,
        CheckinEntity::class,
        PointHistoryEntity::class,
        FoodStoreReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun placeDao(): PlaceDao

    abstract fun foodStoreDao(): FoodStoreDao

    abstract fun foodItemDao(): FoodItemDao

    abstract fun checkinDao(): CheckinDao

    abstract fun pointHistoryDao(): PointHistoryDao

    abstract fun foodStoreReviewDao(): FoodStoreReviewDao
}
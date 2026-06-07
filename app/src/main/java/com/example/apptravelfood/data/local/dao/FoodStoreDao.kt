package com.example.apptravelfood.data.local.dao

import androidx.room.*
import com.example.apptravelfood.data.local.entity.FoodStoreEntity

@Dao
interface FoodStoreDao {

    @Insert
    suspend fun insertFoodStore(foodStore: FoodStoreEntity): Long

    @Update
    suspend fun updateFoodStore(foodStore: FoodStoreEntity)

    @Delete
    suspend fun deleteFoodStore(foodStore: FoodStoreEntity)

    @Query("SELECT * FROM food_stores WHERE placeId = :placeId ORDER BY createdAt DESC")
    suspend fun getFoodStoresByPlaceId(placeId: String): List<FoodStoreEntity>

    @Query("SELECT * FROM food_stores WHERE createdByUserId = :userId ORDER BY createdAt DESC")
    suspend fun getFoodStoresByUserId(userId: Long): List<FoodStoreEntity>

    @Query("SELECT * FROM food_stores WHERE foodStoreId = :foodStoreId LIMIT 1")
    suspend fun getFoodStoreById(foodStoreId: Long): FoodStoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodStoreReplace(foodStore: FoodStoreEntity): Long
}
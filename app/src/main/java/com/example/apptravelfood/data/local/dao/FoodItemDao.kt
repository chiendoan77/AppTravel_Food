package com.example.apptravelfood.data.local.dao

import androidx.room.*
import com.example.apptravelfood.data.local.entity.FoodItemEntity

@Dao
interface FoodItemDao {

    @Insert
    suspend fun insertFoodItem(foodItem: FoodItemEntity): Long

    @Update
    suspend fun updateFoodItem(foodItem: FoodItemEntity)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItemEntity)

    @Query("SELECT * FROM food_items WHERE foodStoreId = :foodStoreId ORDER BY createdAt DESC")
    suspend fun getFoodItemsByStoreId(foodStoreId: Long): List<FoodItemEntity>
}
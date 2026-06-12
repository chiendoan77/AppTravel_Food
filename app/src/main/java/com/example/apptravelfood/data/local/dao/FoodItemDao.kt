package com.example.apptravelfood.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM food_items WHERE foodItemId = :foodItemId LIMIT 1")
    suspend fun getFoodItemById(foodItemId: Long): FoodItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItemReplace(foodItem: FoodItemEntity): Long
}
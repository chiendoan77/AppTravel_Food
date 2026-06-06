package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.FoodItemDao
import com.example.apptravelfood.data.local.entity.FoodItemEntity

class FoodItemRepository(
    private val foodItemDao: FoodItemDao
) {
    suspend fun addFoodItem(item: FoodItemEntity): Long {
        return foodItemDao.insertFoodItem(item)
    }

    suspend fun updateFoodItem(item: FoodItemEntity) {
        foodItemDao.updateFoodItem(item)
    }

    suspend fun deleteFoodItem(item: FoodItemEntity) {
        foodItemDao.deleteFoodItem(item)
    }

    suspend fun getItemsByStore(foodStoreId: Long): List<FoodItemEntity> {
        return foodItemDao.getFoodItemsByStoreId(foodStoreId)
    }
}
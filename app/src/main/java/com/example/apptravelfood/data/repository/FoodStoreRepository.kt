package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.FoodStoreDao
import com.example.apptravelfood.data.local.entity.FoodStoreEntity

class FoodStoreRepository(
    private val foodStoreDao: FoodStoreDao
) {
    suspend fun addFoodStore(store: FoodStoreEntity): Long {
        return foodStoreDao.insertFoodStore(store)
    }

    suspend fun updateFoodStore(store: FoodStoreEntity) {
        foodStoreDao.updateFoodStore(store)
    }

    suspend fun deleteFoodStore(store: FoodStoreEntity) {
        foodStoreDao.deleteFoodStore(store)
    }

    suspend fun getStoresByPlace(placeId: String): List<FoodStoreEntity> {
        return foodStoreDao.getFoodStoresByPlaceId(placeId)
    }

    suspend fun getStoresByUser(userId: Long): List<FoodStoreEntity> {
        return foodStoreDao.getFoodStoresByUserId(userId)
    }

    suspend fun getFoodStoreById(foodStoreId: Long): FoodStoreEntity? {
        return foodStoreDao.getFoodStoreById(foodStoreId)
    }

    suspend fun insertFoodStoreReplace(foodStore: FoodStoreEntity): Long {
        return foodStoreDao.insertFoodStoreReplace(foodStore)
    }
}
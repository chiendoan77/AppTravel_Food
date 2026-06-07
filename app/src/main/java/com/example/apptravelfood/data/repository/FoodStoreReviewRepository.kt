package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.local.dao.FoodStoreReviewDao
import com.example.apptravelfood.data.local.entity.FoodStoreReviewEntity

class FoodStoreReviewRepository(
    private val reviewDao: FoodStoreReviewDao
) {

    suspend fun getReviewsByStore(
        foodStoreId: Long
    ): List<FoodStoreReviewEntity> {
        return reviewDao.getReviewsByStore(foodStoreId)
    }

    suspend fun getMyReview(
        foodStoreId: Long,
        userId: Long
    ): FoodStoreReviewEntity? {
        return reviewDao.getMyReview(foodStoreId, userId)
    }

    suspend fun saveOrUpdateReview(
        foodStoreId: Long,
        userId: Long,
        rating: Float,
        comment: String
    ) {
        val oldReview = reviewDao.getMyReview(
            foodStoreId = foodStoreId,
            userId = userId
        )

        if (oldReview == null) {
            reviewDao.insertReview(
                FoodStoreReviewEntity(
                    foodStoreId = foodStoreId,
                    userId = userId,
                    rating = rating,
                    comment = comment
                )
            )
        } else {
            reviewDao.updateReview(
                oldReview.copy(
                    rating = rating,
                    comment = comment,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun deleteMyReview(
        reviewId: Long,
        userId: Long
    ) {
        reviewDao.deleteMyReview(
            reviewId = reviewId,
            userId = userId
        )
    }

    suspend fun getAverageRating(foodStoreId: Long): Float {
        return reviewDao.getAverageRating(foodStoreId) ?: 0f
    }

    suspend fun getReviewCount(foodStoreId: Long): Int {
        return reviewDao.getReviewCount(foodStoreId)
    }
    suspend fun getReviewById(reviewId: Long): FoodStoreReviewEntity? {
        return reviewDao.getReviewById(reviewId)
    }

    suspend fun insertReviewReplace(review: FoodStoreReviewEntity): Long {
        return reviewDao.insertReviewReplace(review)
    }
}
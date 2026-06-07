package com.example.apptravelfood.data.local.dao

import androidx.room.*
import com.example.apptravelfood.data.local.entity.FoodStoreReviewEntity

@Dao
interface FoodStoreReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: FoodStoreReviewEntity): Long

    @Update
    suspend fun updateReview(review: FoodStoreReviewEntity)

    @Query("""
        SELECT *
        FROM food_store_reviews
        WHERE foodStoreId = :foodStoreId
        ORDER BY updatedAt DESC
    """)
    suspend fun getReviewsByStore(
        foodStoreId: Long
    ): List<FoodStoreReviewEntity>

    @Query("""
        SELECT *
        FROM food_store_reviews
        WHERE foodStoreId = :foodStoreId
        AND userId = :userId
        LIMIT 1
    """)
    suspend fun getMyReview(
        foodStoreId: Long,
        userId: Long
    ): FoodStoreReviewEntity?

    @Query("""
        DELETE FROM food_store_reviews
        WHERE reviewId = :reviewId
        AND userId = :userId
    """)
    suspend fun deleteMyReview(
        reviewId: Long,
        userId: Long
    )

    @Query("""
        SELECT AVG(rating)
        FROM food_store_reviews
        WHERE foodStoreId = :foodStoreId
    """)
    suspend fun getAverageRating(foodStoreId: Long): Float?

    @Query("""
        SELECT COUNT(*)
        FROM food_store_reviews
        WHERE foodStoreId = :foodStoreId
    """)
    suspend fun getReviewCount(foodStoreId: Long): Int

    @Query("SELECT * FROM food_store_reviews WHERE reviewId = :reviewId LIMIT 1")
    suspend fun getReviewById(reviewId: Long): FoodStoreReviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewReplace(review: FoodStoreReviewEntity): Long
}
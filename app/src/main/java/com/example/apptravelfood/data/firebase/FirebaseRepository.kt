package com.example.apptravelfood.data.firebase

import com.example.apptravelfood.data.local.entity.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun backupUser(user: UserEntity) {
        db.collection("users")
            .document(user.userId.toString())
            .set(user)
            .await()
    }

    suspend fun backupFoodStore(store: FoodStoreEntity) {
        db.collection("food_stores")
            .document(store.foodStoreId.toString())
            .set(store)
            .await()
    }

    suspend fun backupFoodItem(item: FoodItemEntity) {
        db.collection("food_items")
            .document(item.foodItemId.toString())
            .set(item)
            .await()
    }

    suspend fun backupReview(review: FoodStoreReviewEntity) {
        db.collection("food_store_reviews")
            .document(review.reviewId.toString())
            .set(review)
            .await()
    }

    suspend fun backupCheckin(checkin: CheckinEntity) {
        db.collection("checkins")
            .document(checkin.checkinId.toString())
            .set(checkin)
            .await()
    }

    suspend fun backupPointHistory(history: PointHistoryEntity) {
        db.collection("point_history")
            .document(history.pointHistoryId.toString())
            .set(history)
            .await()
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        val snapshot = db.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()

        return snapshot.documents
            .firstOrNull()
            ?.toObject(UserEntity::class.java)
    }

    suspend fun getFoodStoresByUserId(userId: Long): List<FoodStoreEntity> {
        val snapshot = db.collection("food_stores")
            .whereEqualTo("createdByUserId", userId)
            .get()
            .await()

        return snapshot.toObjects(FoodStoreEntity::class.java)
    }

    suspend fun deleteReview(reviewId: Long) {
        db.collection("food_store_reviews")
            .document(reviewId.toString())
            .delete()
            .await()
    }

    suspend fun getFoodItemsByStoreIds(
        storeIds: List<Long>
    ): List<FoodItemEntity> {
        if (storeIds.isEmpty()) return emptyList()

        val result = mutableListOf<FoodItemEntity>()

        storeIds.chunked(10).forEach { chunk ->
            val snapshot = db.collection("food_items")
                .whereIn("foodStoreId", chunk)
                .get()
                .await()

            result.addAll(
                snapshot.toObjects(FoodItemEntity::class.java)
            )
        }

        return result
    }

    suspend fun getReviewsByUserId(userId: Long): List<FoodStoreReviewEntity> {
        val snapshot = db.collection("food_store_reviews")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.toObjects(FoodStoreReviewEntity::class.java)
    }

    suspend fun getCheckinsByUserId(userId: Long): List<CheckinEntity> {
        val snapshot = db.collection("checkins")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.toObjects(CheckinEntity::class.java)
    }

    suspend fun getPointHistoryByUserId(userId: Long): List<PointHistoryEntity> {
        val snapshot = db.collection("point_history")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.toObjects(PointHistoryEntity::class.java)
    }
    suspend fun getFoodStoresByPlaceId(placeId: String): List<FoodStoreEntity> {
        val snapshot = db.collection("food_stores")
            .whereEqualTo("placeId", placeId)
            .get()
            .await()

        return snapshot.toObjects(FoodStoreEntity::class.java)
    }
    suspend fun getReviewsByFoodStoreId(foodStoreId: Long): List<FoodStoreReviewEntity> {
        val snapshot = db.collection("food_store_reviews")
            .whereEqualTo("foodStoreId", foodStoreId)
            .get()
            .await()

        return snapshot.toObjects(FoodStoreReviewEntity::class.java)
    }
    suspend fun deleteFoodItem(foodItemId: Long) {
        db.collection("food_items")
            .document(foodItemId.toString())
            .delete()
            .await()
    }
}
package com.example.apptravelfood.data.repository

import android.util.Log
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.local.entity.PlaceEntity

class SyncRepository(
    private val firebaseRepository: FirebaseRepository,
    private val userRepository: UserRepository,
    private val foodStoreRepository: FoodStoreRepository,
    private val foodItemRepository: FoodItemRepository,
    private val reviewRepository: FoodStoreReviewRepository,
    private val checkinRepository: CheckinRepository,
    private val pointHistoryRepository: PointHistoryRepository,
    private val placeRepository: PlaceRepositoryLocal
) {

    suspend fun syncAfterLogin(email: String): Long? {
        val firebaseUser =
            firebaseRepository.getUserByEmail(email)
                ?: return null

        Log.d("SYNC", "insert user ${firebaseUser.userId}")

        userRepository.insertUserReplace(firebaseUser)

        val localUser =
            userRepository.getUser(firebaseUser.userId)

        if (localUser == null) {
            Log.e("SYNC", "User insert failed")
            return null
        }

        val userId = firebaseUser.userId

        Log.d("SYNC", "sync stores")
        syncFoodStores(userId)

        Log.d("SYNC", "sync reviews")
        syncReviews(userId)

        Log.d("SYNC", "sync checkins")
        syncCheckins(userId)

        Log.d("SYNC", "sync point history")
        syncPointHistory(userId)

        return userId
    }

    private suspend fun syncFoodStores(userId: Long) {
        val stores =
            firebaseRepository.getFoodStoresByUserId(userId)

        stores.forEach { store ->
            Log.d(
                "SYNC",
                "insert store=${store.foodStoreId}, createdBy=${store.createdByUserId}"
            )

            try {
                // Đảm bảo place tồn tại trước khi chèn store (tránh lỗi Foreign Key)
                placeRepository.savePlace(PlaceEntity(placeId = store.placeId))

                foodStoreRepository.insertFoodStoreReplace(store)
                Log.d("SYNC", "insert store ok=${store.foodStoreId}")
            } catch (e: Exception) {
                Log.e("SYNC", "insert store failed=${store.foodStoreId}", e)
            }
        }

        val storeIds = stores.map { it.foodStoreId }

        val foodItems =
            firebaseRepository.getFoodItemsByStoreIds(storeIds)

        foodItems.forEach { item ->
            Log.d(
                "SYNC",
                "insert item=${item.foodItemId}, storeId=${item.foodStoreId}"
            )

            try {
                val store =
                    foodStoreRepository.getFoodStoreById(item.foodStoreId)

                if (store != null) {
                    foodItemRepository.insertFoodItemReplace(item)
                    Log.d("SYNC", "insert item ok=${item.foodItemId}")
                } else {
                    Log.e("SYNC", "skip item missing store=${item.foodStoreId}")
                }
            } catch (e: Exception) {
                Log.e("SYNC", "insert item failed=${item.foodItemId}", e)
            }
        }
    }

    private suspend fun syncReviews(userId: Long) {
        val reviews =
            firebaseRepository.getReviewsByUserId(userId)

        reviews.forEach { review ->
            val user =
                userRepository.getUser(review.userId)

            var store =
                foodStoreRepository.getFoodStoreById(review.foodStoreId)

            // Nếu store chưa có local (ví dụ review quán người khác), ta cần tạo placeholder store
            // để tránh lỗi Foreign Key Constraint trong Room
            if (store == null) {
                try {
                    // Cần có Place placeholder trước
                    placeRepository.savePlace(PlaceEntity(placeId = "placeholder_${review.foodStoreId}"))

                    val placeholderStore =
                        com.example.apptravelfood.data.local.entity.FoodStoreEntity(
                            foodStoreId = review.foodStoreId,
                            placeId = "placeholder_${review.foodStoreId}",
                            name = "Quán ăn đang đồng bộ...",
                            createdByUserId = 0 // System
                        )
                    foodStoreRepository.insertFoodStoreReplace(placeholderStore)
                    store = placeholderStore
                } catch (e: Exception) {
                    Log.e(
                        "SYNC",
                        "Failed to create placeholder store for review ${review.reviewId}",
                        e
                    )
                }
            }

            if (user != null && store != null) {
                reviewRepository.insertReviewReplace(review)
            } else {
                Log.e(
                    "SYNC",
                    "Skip review ${review.reviewId}, user=${review.userId}, store=${review.foodStoreId}"
                )
            }
        }
    }

    private suspend fun syncCheckins(userId: Long) {
        val user =
            userRepository.getUser(userId)

        if (user == null) {
            Log.e("SYNC", "Skip checkins, missing user $userId")
            return
        }

        val checkins =
            firebaseRepository.getCheckinsByUserId(userId)

        checkins.forEach { checkin ->
            checkinRepository.insertCheckinReplace(checkin)
        }
    }

    private suspend fun syncPointHistory(userId: Long) {
        val user =
            userRepository.getUser(userId)

        if (user == null) {
            Log.e("SYNC", "Skip point history, missing user $userId")
            return
        }

        val histories =
            firebaseRepository.getPointHistoryByUserId(userId)

        histories.forEach { history ->
            pointHistoryRepository.insertPointHistoryReplace(history)
        }
    }

    suspend fun syncAfterLoginByUserId(userId: Long) {
        val user =
            userRepository.getUser(userId)

        if (user == null) return

        syncCheckins(userId)
        syncPointHistory(userId)
    }
}
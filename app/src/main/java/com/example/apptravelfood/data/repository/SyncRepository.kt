package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.firebase.FirebaseRepository

class SyncRepository(
    private val firebaseRepository: FirebaseRepository,
    private val userRepository: UserRepository,
    private val foodStoreRepository: FoodStoreRepository,
    private val foodItemRepository: FoodItemRepository,
    private val reviewRepository: FoodStoreReviewRepository,
    private val checkinRepository: CheckinRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {

    suspend fun syncAfterLogin(email: String): Long? {
        val firebaseUser =
            firebaseRepository.getUserByEmail(email)
                ?: return null

        userRepository.insertUserReplace(firebaseUser)

        val userId = firebaseUser.userId

        syncFoodStores(userId)
        syncReviews(userId)
        syncCheckins(userId)
        syncPointHistory(userId)

        return userId
    }

    private suspend fun syncFoodStores(userId: Long) {
        val stores =
            firebaseRepository.getFoodStoresByUserId(userId)

        stores.forEach { store ->
            val existed =
                foodStoreRepository.getFoodStoreById(
                    store.foodStoreId
                )

            if (existed == null) {
                foodStoreRepository.insertFoodStoreReplace(store)
            }
        }

        val storeIds =
            stores.map { it.foodStoreId }

        val foodItems =
            firebaseRepository.getFoodItemsByStoreIds(storeIds)

        foodItems.forEach { item ->
            val existed =
                foodItemRepository.getFoodItemById(
                    item.foodItemId
                )

            if (existed == null) {
                foodItemRepository.insertFoodItemReplace(item)
            }
        }
    }

    private suspend fun syncReviews(userId: Long) {
        val reviews =
            firebaseRepository.getReviewsByUserId(userId)

        reviews.forEach { review ->

            val store =
                foodStoreRepository.getFoodStoreById(
                    review.foodStoreId
                )

            if (store == null) {
                return@forEach
            }

            val existed =
                reviewRepository.getReviewById(
                    review.reviewId
                )

            if (existed == null) {
                reviewRepository.insertReviewReplace(review)
            }
        }
    }

    private suspend fun syncCheckins(userId: Long) {
        val checkins =
            firebaseRepository.getCheckinsByUserId(userId)

        checkins.forEach { checkin ->
            val existed =
                checkinRepository.getCheckinById(
                    checkin.checkinId
                )

            if (existed == null) {
                checkinRepository.insertCheckinReplace(checkin)
            }
        }
    }

    private suspend fun syncPointHistory(userId: Long) {
        val histories =
            firebaseRepository.getPointHistoryByUserId(userId)

        histories.forEach { history ->
            val existed =
                pointHistoryRepository.getPointHistoryById(
                    history.pointHistoryId
                )

            if (existed == null) {
                pointHistoryRepository.insertPointHistoryReplace(history)
            }
        }
    }

    suspend fun syncAfterLoginByUserId(userId: Long) {
        syncCheckins(userId)
        syncPointHistory(userId)
    }
}
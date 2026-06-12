package com.example.apptravelfood.data.firebase

import android.net.Uri
import android.util.Log
import com.example.apptravelfood.data.local.entity.CheckinEntity
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.local.entity.FoodStoreReviewEntity
import com.example.apptravelfood.data.local.entity.PointHistoryEntity
import com.example.apptravelfood.data.local.entity.UserEntity
import com.example.apptravelfood.data.remote.dto.AppNotificationDto
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun backupUser(user: UserEntity) {
        db.collection("users")
            .document(user.email)
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
        val doc = db.collection("users")
            .document(email)
            .get()
            .await()

        return doc.toObject(UserEntity::class.java)
    }

    suspend fun getUserById(userId: Long): UserEntity? {
        val snapshot = db.collection("users")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(UserEntity::class.java)
    }

    suspend fun getFoodStoresByUserId(userId: Long): List<FoodStoreEntity> {
        val snapshot = db.collection("food_stores")
            .whereEqualTo("createdByUserId", userId)
            .get()
            .await()

        return snapshot.toObjects(FoodStoreEntity::class.java)
    }

    suspend fun deleteFoodStore(foodStoreId: Long) {
        db.collection("food_stores")
            .document(foodStoreId.toString())
            .delete()
            .await()
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

    suspend fun getReviewsByFoodStoreId(foodStoreId: Long): List<FoodStoreReviewEntity> {
        val snapshot = db.collection("food_store_reviews")
            .whereEqualTo("foodStoreId", foodStoreId)
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

    suspend fun deleteFoodItem(foodItemId: Long) {
        db.collection("food_items")
            .document(foodItemId.toString())
            .delete()
            .await()
    }

    suspend fun uploadFoodItemImage(
        foodItemId: Long,
        imageUri: Uri
    ): String {
        val ref = storage.reference
            .child("food_items")
            .child("item_$foodItemId.jpg")

        ref.putFile(imageUri).await()

        return ref.downloadUrl.await().toString()
    }

    suspend fun getTodayCheckin(
        userId: Long,
        startOfDay: Long,
        endOfDay: Long
    ): CheckinEntity? {
        val snapshot = db.collection("checkins")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.toObjects(CheckinEntity::class.java)
            .firstOrNull { checkin ->
                checkin.checkinTime in startOfDay..endOfDay
            }
    }

    suspend fun createNotification(
        notification: AppNotificationDto
    ) {
        Log.d(
            "FirebaseRepository",
            "Creating notification in Firebase for receiver=${notification.receiverUserId}"
        )
        val doc = db.collection("notifications").document()

        doc.set(
            notification.copy(
                notificationId = doc.id
            )
        ).await()
        Log.d("FirebaseRepository", "Notification created successfully with ID=${doc.id}")
    }

    fun listenUnreadNotifications(
        userId: Long,
        onNewNotification: (AppNotificationDto) -> Unit
    ): ListenerRegistration {
        Log.d("!!!NOTI!!!", "FirebaseRepository: listenUnreadNotifications for userId=$userId")

        // Listen to all notifications for this receiver
        return db.collection("notifications")
            .whereEqualTo("receiverUserId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(
                        "!!!NOTI!!!",
                        "FirebaseRepository: Listen failed error=${error.message}",
                        error
                    )
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Log.d("!!!NOTI!!!", "FirebaseRepository: Snapshot is null")
                    return@addSnapshotListener
                }

                Log.d(
                    "!!!NOTI!!!",
                    "FirebaseRepository: Received snapshot. Total docs=${snapshot.size()}. Changes=${snapshot.documentChanges.size}"
                )

                snapshot.documentChanges.forEach { change ->
                    if (change.type == DocumentChange.Type.ADDED) {
                        val item = change.document
                            .toObject(AppNotificationDto::class.java)

                        Log.d(
                            "!!!NOTI!!!",
                            "FirebaseRepository: New doc ADDED: isRead=${item.isRead}, title=${item.title}"
                        )

                        // Lọc isRead thủ công hoặc giữ query cũ.
                        // Tạm thời bỏ filter isRead trong query để xem có doc nào về không.
                        if (!item.isRead) {
                            Log.d(
                                "!!!NOTI!!!",
                                "FirebaseRepository: Notifying about unread item: ${item.notificationId}"
                            )
                            onNewNotification(item)
                        }
                    }
                }
            }
    }
}
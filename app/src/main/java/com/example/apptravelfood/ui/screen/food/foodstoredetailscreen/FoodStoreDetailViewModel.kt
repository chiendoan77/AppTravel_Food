package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.local.entity.UserEntity
import com.example.apptravelfood.data.remote.dto.AppNotificationDto
import com.example.apptravelfood.data.repository.FoodItemRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import com.example.apptravelfood.data.repository.FoodStoreReviewRepository
import com.example.apptravelfood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FoodStoreDetailViewModel(
    private val foodItemRepository: FoodItemRepository,
    private val foodStoreRepository: FoodStoreRepository,
    private val reviewRepository: FoodStoreReviewRepository,
    private val firebaseRepository: FirebaseRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodStoreDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadStoreDetail(
        store: FoodStoreEntity,
        userId: Long
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val isOwner = store.createdByUserId == userId
                val foods = foodItemRepository.getItemsByStore(store.foodStoreId)

                val remoteReviews = firebaseRepository.getReviewsByFoodStoreId(store.foodStoreId)
                val remoteReviewIds = remoteReviews.map { it.reviewId }.toSet()

                val localReviews = reviewRepository.getReviewsByStore(store.foodStoreId)
                localReviews.forEach { localReview ->
                    if (!remoteReviewIds.contains(localReview.reviewId)) {
                        reviewRepository.deleteMyReview(localReview.reviewId, localReview.userId)
                    }
                }

                remoteReviews.forEach { review ->
                    reviewRepository.insertReviewReplace(review)
                }

                val reviews = reviewRepository.getReviewsByStore(store.foodStoreId)
                val userIds = reviews.map { it.userId }.distinct()
                val userMap = mutableMapOf<Long, UserEntity>()
                userIds.forEach { uid ->
                    var user = userRepository.getUser(uid)
                    if (user == null) {
                        // Nếu local chưa có, thử lấy từ Firebase
                        try {
                            user = firebaseRepository.getUserById(uid)
                            user?.let { userRepository.insertUserReplace(it) }
                        } catch (_: Exception) {
                        }
                    }
                    user?.let { userMap[uid] = it }
                }

                val myReview = reviewRepository.getMyReview(store.foodStoreId, userId)
                val avg = reviewRepository.getAverageRating(store.foodStoreId)
                val count = reviewRepository.getReviewCount(store.foodStoreId)

                _uiState.value = FoodStoreDetailUiState(
                    isOwner = isOwner,
                    isLoading = false,
                    store = store,
                    foodItems = foods,
                    reviews = reviews,
                    reviewUsers = userMap,
                    myReview = myReview,
                    averageRating = avg,
                    reviewCount = count,
                    ratingInput = myReview?.rating ?: 5f,
                    commentInput = myReview?.comment ?: ""
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    store = store,
                    error = e.message ?: "Không tải được chi tiết quán"
                )
            }
        }
    }

    fun updateRating(value: Float) {
        _uiState.value = _uiState.value.copy(
            ratingInput = value
        )
    }

    fun updateComment(value: String) {
        _uiState.value = _uiState.value.copy(
            commentInput = value
        )
    }

    fun saveReview(userId: Long) {
        val state = _uiState.value
        val store = state.store ?: return

        if (state.commentInput.isBlank()) {
            _uiState.value = state.copy(
                error = "Vui lòng nhập bình luận"
            )
            return
        }

        viewModelScope.launch {
            try {
                reviewRepository.saveOrUpdateReview(
                    foodStoreId = store.foodStoreId,
                    userId = userId,
                    rating = state.ratingInput,
                    comment = state.commentInput
                )

                val updatedReview =
                    reviewRepository.getMyReview(
                        foodStoreId = store.foodStoreId,
                        userId = userId
                    )
                val currentUserName = userRepository.getUser(userId)

                if (updatedReview != null) {
                    try {
                        firebaseRepository.backupReview(updatedReview)

                        if (store.createdByUserId != userId) {
                            val senderName = currentUserName?.fullName ?: "Người dùng"
                            firebaseRepository.createNotification(
                                AppNotificationDto(
                                    receiverUserId = store.createdByUserId,
                                    senderUserId = userId,
                                    senderName = senderName,
                                    title = "Có đánh giá mới",
                                    message = "$senderName đã đánh giá quán của bạn",
                                    type = "REVIEW",
                                    foodStoreId = store.foodStoreId,
                                    reviewId = updatedReview.reviewId
                                )
                            )
                            Log.d(
                                "FoodStoreDetailViewModel",
                                "Notification created for receiverUserId=${store.createdByUserId}"
                            )
                        }

                    } catch (e: Exception) {
                        Log.e("FoodStoreDetailViewModel", "Error creating notification", e)
                    }
                }

                loadStoreDetail(
                    store = store,
                    userId = userId
                )

                _uiState.value = _uiState.value.copy(
                    message = "Đã lưu đánh giá"
                )

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    error = e.message ?: "Không lưu được đánh giá"
                )
            }
        }
    }

    fun deleteMyReview(userId: Long) {
        val state = _uiState.value
        val store = state.store ?: return
        val myReview = state.myReview ?: return

        viewModelScope.launch {
            try {
                reviewRepository.deleteMyReview(
                    reviewId = myReview.reviewId,
                    userId = userId
                )

                try {
                    firebaseRepository.deleteReview(
                        myReview.reviewId
                    )
                } catch (_: Exception) {
                }

                loadStoreDetail(
                    store = store,
                    userId = userId
                )

                _uiState.value = _uiState.value.copy(
                    message = "Đã xóa đánh giá"
                )

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    error = e.message ?: "Không xóa được đánh giá"
                )
            }
        }
    }

    fun deleteFoodStore(onSuccess: () -> Unit) {
        val store = _uiState.value.store ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                firebaseRepository.deleteFoodStore(store.foodStoreId)
                foodStoreRepository.deleteFoodStore(store)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Đã xóa quán ăn thành công"
                )

                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không thể xóa quán ăn"
                )
            }
        }
    }
}
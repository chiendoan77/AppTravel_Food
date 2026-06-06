package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.repository.FoodItemRepository
import com.example.apptravelfood.data.repository.FoodStoreReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FoodStoreDetailViewModel(
    private val foodItemRepository: FoodItemRepository,
    private val reviewRepository: FoodStoreReviewRepository
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
                val foods = foodItemRepository.getItemsByStore(store.foodStoreId)
                val reviews = reviewRepository.getReviewsByStore(store.foodStoreId)
                val myReview = reviewRepository.getMyReview(store.foodStoreId, userId)
                val avg = reviewRepository.getAverageRating(store.foodStoreId)
                val count = reviewRepository.getReviewCount(store.foodStoreId)

                _uiState.value = FoodStoreDetailUiState(
                    isLoading = false,
                    store = store,
                    foodItems = foods,
                    reviews = reviews,
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
}
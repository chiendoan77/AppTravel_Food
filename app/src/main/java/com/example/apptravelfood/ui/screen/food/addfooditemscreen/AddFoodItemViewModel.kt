package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.repository.FoodItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddFoodItemViewModel(
    private val foodItemRepository: FoodItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFoodItemUiState())
    val uiState = _uiState.asStateFlow()

    fun setEditFoodItem(food: FoodItemEntity) {
        _uiState.value = AddFoodItemUiState(
            foodItemId = food.foodItemId,
            name = food.name,
            description = food.description ?: "",
            price = food.price?.toString() ?: "",
            imageUrl = food.imageUrl ?: "",
            isEditMode = true
        )
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun updatePrice(value: String) {
        _uiState.value = _uiState.value.copy(price = value)
    }

    fun updateImageUrl(value: String) {
        _uiState.value = _uiState.value.copy(imageUrl = value)
    }

    fun saveFoodItem(foodStoreId: Long) {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Tên món ăn không được để trống")
            return
        }

        val priceValue = state.price.toDoubleOrNull()

        if (priceValue == null || priceValue < 0) {
            _uiState.value = state.copy(error = "Giá món ăn không hợp lệ")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isSaving = true, error = null)

                if (state.isEditMode && state.foodItemId != null) {
                    foodItemRepository.updateFoodItem(
                        FoodItemEntity(
                            foodItemId = state.foodItemId,
                            foodStoreId = foodStoreId,
                            name = state.name,
                            description = state.description.ifBlank { null },
                            price = priceValue,
                            imageUrl = state.imageUrl.ifBlank { null }
                        )
                    )
                } else {
                    foodItemRepository.addFoodItem(
                        FoodItemEntity(
                            foodStoreId = foodStoreId,
                            name = state.name,
                            description = state.description.ifBlank { null },
                            price = priceValue,
                            imageUrl = state.imageUrl.ifBlank { null }
                        )
                    )
                }

                _uiState.value = state.copy(
                    isSaving = false,
                    success = true
                )

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSaving = false,
                    error = e.message ?: "Không thể lưu món ăn"
                )
            }
        }
    }

    fun deleteFoodItem(
        foodStoreId: Long,
        onDeleted: () -> Unit
    ) {
        val state = _uiState.value
        val foodItemId = state.foodItemId ?: return

        viewModelScope.launch {
            try {
                foodItemRepository.deleteFoodItem(
                    FoodItemEntity(
                        foodItemId = foodItemId,
                        foodStoreId = foodStoreId,
                        name = state.name,
                        description = state.description.ifBlank { null },
                        price = state.price.toDoubleOrNull(),
                        imageUrl = state.imageUrl.ifBlank { null }
                    )
                )

                onDeleted()

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    error = e.message ?: "Không thể xóa món ăn"
                )
            }
        }
    }
}
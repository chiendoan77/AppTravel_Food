package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.repository.FoodItemRepository
import com.example.apptravelfood.data.repository.SupabaseStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddFoodItemViewModel(
    private val foodItemRepository: FoodItemRepository,
    private val firebaseRepository: FirebaseRepository,
    private val supabaseStorageRepository: SupabaseStorageRepository
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

    fun saveFoodItem(context: Context, foodStoreId: Long) {
        val state = _uiState.value

        if (state.name.trim().length < 3) {
            _uiState.value = state.copy(error = "Tên món ăn phải có ít nhất 3 ký tự")
            return
        }

        val priceValue = state.price.toDoubleOrNull()
        if (priceValue == null || priceValue < 1000) {
            _uiState.value = state.copy(error = "Giá món ăn không hợp lệ (tối thiểu 1.000đ)")
            return
        }

        if (state.description.isNotBlank() && state.description.trim().length < 5) {
            _uiState.value = state.copy(error = "Mô tả món ăn (nếu có) phải từ 5 ký tự trở lên")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isSaving = true, error = null)

                if (state.isEditMode && state.foodItemId != null) {
                    var finalImageUrl =
                        state.imageUrl.ifBlank { null }

                    if (state.localImageUri != null) {
                        finalImageUrl =
                            firebaseRepository.uploadFoodItemImage(
                                foodItemId = state.foodItemId,
                                imageUri = state.localImageUri
                            )
                    }

                    val updateItem = FoodItemEntity(
                        foodItemId = state.foodItemId,
                        foodStoreId = foodStoreId,
                        name = state.name,
                        description = state.description.ifBlank { null },
                        price = priceValue,
                        imageUrl = finalImageUrl
                    )

                    foodItemRepository.updateFoodItem(updateItem)

                    try {
                        firebaseRepository.backupFoodItem(updateItem)
                    } catch (_: Exception) {
                    }

                } else {

                    val itemWithoutId = FoodItemEntity(
                        foodStoreId = foodStoreId,
                        name = state.name,
                        description = state.description.ifBlank { null },
                        price = priceValue,
                        imageUrl = state.imageUrl.ifBlank { null }
                    )

                    val newId = foodItemRepository.addFoodItem(itemWithoutId)

                    var finalImageUrl =
                        state.imageUrl.ifBlank { null }

                    if (state.localImageUri != null) {
                        finalImageUrl =
                            supabaseStorageRepository.uploadFoodItemImage(
                                foodItemId = newId,
                                imageUri = state.localImageUri,
                                context = context
                            )
                    }

                    val itemWithId = itemWithoutId.copy(
                        foodItemId = newId,
                        imageUrl = finalImageUrl
                    )
                    foodItemRepository.updateFoodItem(itemWithId)
                    try {
                        firebaseRepository.backupFoodItem(itemWithId)
                    } catch (_: Exception) {
                    }
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

        val deleteItem = FoodItemEntity(
            foodItemId = foodItemId,
            foodStoreId = foodStoreId,
            name = state.name,
            description = state.description.ifBlank { null },
            price = state.price.toDoubleOrNull(),
            imageUrl = state.imageUrl.ifBlank { null }
        )

        viewModelScope.launch {
            try {
                foodItemRepository.deleteFoodItem(deleteItem)

                try {
                    firebaseRepository.deleteFoodItem(foodItemId)
                } catch (_: Exception) {
                }

                onDeleted()

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    error = e.message ?: "Không thể xóa món ăn"
                )
            }
        }
    }

    fun updateLocalImage(uri: Uri?) {
        _uiState.value = _uiState.value.copy(
            localImageUri = uri
        )
    }
}
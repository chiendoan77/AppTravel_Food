package com.example.apptravelfood.ui.screen.food.addfoodstorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddFoodStoreViewModel(
    private val foodStoreRepository: FoodStoreRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFoodStoreUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updateAddress(value: String) {
        _uiState.value = _uiState.value.copy(address = value)
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun updateImageUrl(value: String) {
        _uiState.value = _uiState.value.copy(imageUrl = value)
    }

    fun saveFoodStore(
        placeId: String,
        userId: Long
    ) {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = state.copy(
                error = "Tên quán không được để trống"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(
                    isSaving = true,
                    error = null
                )

                val storeWithoutId = FoodStoreEntity(
                    placeId = placeId,
                    createdByUserId = userId,
                    name = state.name,
                    address = state.address.ifBlank { null },
                    imageUrl = state.imageUrl.ifBlank { null },
                    description = state.description.ifBlank { null }
                )

                val newId = foodStoreRepository.addFoodStore(
                    storeWithoutId
                )

                val storeWithId = storeWithoutId.copy(
                    foodStoreId = newId
                )

                try {
                    firebaseRepository.backupFoodStore(
                        storeWithId
                    )
                } catch (firebaseError: Exception) {
                    _uiState.value = state.copy(
                        isSaving = false,
                        error = firebaseError.message ?: "Lỗi đồng bộ dữ liệu lên Firebase"
                    )
                }

                _uiState.value = AddFoodStoreUiState(
                    success = true
                )

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSaving = false,
                    error = e.message ?: "Không thể thêm quán"
                )
            }
        }
    }
}
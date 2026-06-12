package com.example.apptravelfood.ui.screen.food.addfoodstorescreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.repository.AddressRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import com.example.apptravelfood.data.repository.SupabaseStorageRepository
import com.example.apptravelfood.domain.model.AddressSuggestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddFoodStoreViewModel(
    private val foodStoreRepository: FoodStoreRepository,
    private val firebaseRepository: FirebaseRepository,
    private val addressRepository: AddressRepository,
    private val supabaseStorageRepository: SupabaseStorageRepository
) : ViewModel() {

    private var addressSearchJob: Job? = null
    private val _uiState = MutableStateFlow(AddFoodStoreUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun updateImageUrl(value: String) {
        _uiState.value = _uiState.value.copy(imageUrl = value)
    }

    fun updateLocalImage(uri: Uri?) {
        _uiState.value = _uiState.value.copy(
            localImageUri = uri
        )
    }

    fun saveFoodStore(
        context: Context,
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

        if (state.address.isBlank()) {
            _uiState.value = state.copy(
                error = "Địa chỉ quán không được để trống"
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
                    latitude = state.latitude,
                    longitude = state.longitude,
                    imageUrl = state.imageUrl.ifBlank { null },
                    description = state.description.ifBlank { null }
                )

                val newId = foodStoreRepository.addFoodStore(
                    storeWithoutId
                )

                var finalImageUrl =
                    state.imageUrl.ifBlank { null }

                if (state.localImageUri != null) {
                    finalImageUrl =
                        supabaseStorageRepository.uploadFoodStoreImage(
                            foodStoreId = newId,
                            imageUri = state.localImageUri,
                            context = context
                        )
                }

                val storeWithId = storeWithoutId.copy(
                    foodStoreId = newId,
                    imageUrl = finalImageUrl
                )

                foodStoreRepository.updateFoodStore(
                    storeWithId
                )

                try {
                    firebaseRepository.backupFoodStore(
                        storeWithId
                    )
                } catch (_: Exception) {
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

    fun updateAddress(value: String) {
        _uiState.value = _uiState.value.copy(
            address = value
        )

        addressSearchJob?.cancel()

        if (value.trim().length < 12) {
            _uiState.value = _uiState.value.copy(
                isSearchingAddress = false
            )
            return
        }

        addressSearchJob = viewModelScope.launch {
            delay(500)

            val state = _uiState.value

            val query = buildString {
                append(state.address.trim())

                if (!state.currentCity.isNullOrBlank()) {
                    append(", ${state.currentCity}")
                }

                if (!state.currentProvince.isNullOrBlank()) {
                    append(", ${state.currentProvince}")
                }
            }

            searchAddressSuggestions(query)
        }
    }

    fun selectAddressSuggestion(
        suggestion: AddressSuggestion
    ) {
        _uiState.value = _uiState.value.copy(
            address = suggestion.address,
            latitude = suggestion.latitude,
            longitude = suggestion.longitude,
            addressSuggestions = emptyList()
        )
    }

    private fun searchAddressSuggestions(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSearchingAddress = true,
                    error = null
                )

                val suggestions = addressRepository.searchAddress(
                    query = query
                )

                _uiState.value = _uiState.value.copy(
                    isSearchingAddress = false,
                    addressSuggestions = suggestions
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearchingAddress = false,
                    addressSuggestions = emptyList(),
                    error = e.message ?: "Không tìm được gợi ý địa chỉ"
                )
            }
        }
    }

    fun updateCurrentLocationSafe(
        address: String?,
        latitude: Double,
        longitude: Double,
        city: String? = null,
        province: String? = null
    ) {
        _uiState.value = _uiState.value.copy(
            address = address
                ?.takeIf { it.isNotBlank() }
                ?: "Vị trí hiện tại: $latitude, $longitude",
            latitude = latitude,
            longitude = longitude,
            currentCity = city,
            currentProvince = province,
            addressSuggestions = emptyList()
        )
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }
}
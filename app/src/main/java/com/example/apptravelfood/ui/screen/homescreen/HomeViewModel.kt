package com.example.apptravelfood.ui.screen.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.local.entity.PlaceEntity
import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import com.example.apptravelfood.data.repository.PlaceRepository
import com.example.apptravelfood.data.repository.PlaceRepositoryLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PlaceRepository,
    private val placeRepositoryLocal: PlaceRepositoryLocal,
    private val foodStoreRepository: FoodStoreRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun searchPlaces(
        query: String,
        location: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val places = repository.sreachPlaces(
                    query = query,
                    location = location,
                    apiKey = AppConstant.API_KEY
                )

                val foodMap = mutableMapOf<String, List<FoodStoreEntity>>()

                places.forEach { place ->

                    val placeId = place.place_id

                    if (!placeId.isNullOrBlank()) {

                        val existedPlace =
                            placeRepositoryLocal.getPlace(placeId)

                        if (existedPlace == null) {
                            placeRepositoryLocal.savePlace(
                                PlaceEntity(
                                    placeId = placeId
                                )
                            )
                        }
                        val firebaseStores =
                            firebaseRepository.getFoodStoresByPlaceId(placeId)

                        firebaseStores.forEach { store ->

                            val safeStore = store.copy(
                                placeId = placeId
                            )

                            val existed =
                                foodStoreRepository.getFoodStoreById(
                                    safeStore.foodStoreId
                                )

                            if (existed == null) {
                                foodStoreRepository.insertFoodStoreReplace(
                                    safeStore
                                )
                            }
                        }

                        val stores =
                            foodStoreRepository.getStoresByPlace(placeId)

                        foodMap[placeId] = stores
                    }
                }

                _uiState.value = HomeUiState(
                    isLoading = false,
                    places = places,
                    foodStoresByPlace = foodMap
                )

            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = e.message ?: "Lỗi không xác định"
                )
            }
        }
    }
}
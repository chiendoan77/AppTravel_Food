package com.example.apptravelfood.ui.screen.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.data.reponsitory.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

//
    fun searchPlaces(
        query: String,
        location: String
    ) {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)

            try {
                val places = repository.sreachPlaces(
                    query = query,
                    location = location,
                    apiKey = AppConstant.API_KEY
                )

                _uiState.value = HomeUiState(
                    isLoading = false,
                    places = places
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
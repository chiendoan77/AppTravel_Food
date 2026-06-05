package com.example.apptravelfood.ui.screen.homescreen

import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.remote.dto.LocalResultsDto

data class HomeUiState(
    val isLoading: Boolean = false,
    val places: List<LocalResultsDto> = emptyList(),
    val foodStoresByPlace: Map<String, List<FoodStoreEntity>> = emptyMap(),
    val error: String? = null
)
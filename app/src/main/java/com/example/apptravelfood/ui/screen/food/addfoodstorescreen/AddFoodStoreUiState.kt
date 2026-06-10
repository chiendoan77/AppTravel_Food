package com.example.apptravelfood.ui.screen.food.addfoodstorescreen

import android.net.Uri
import com.example.apptravelfood.domain.model.AddressSuggestion

data class AddFoodStoreUiState(
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val imageUrl: String = "",

    val latitude: Double? = null,
    val longitude: Double? = null,

    val addressSuggestions: List<AddressSuggestion> = emptyList(),

    val localImageUri: Uri? = null,

    val isSaving: Boolean = false,
    val isSearchingAddress: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
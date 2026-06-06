package com.example.apptravelfood.ui.screen.food.addfoodstorescreen

data class AddFoodStoreUiState(
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isSaving: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
package com.example.apptravelfood.ui.screen.food.addfooditemscreen

data class AddFoodItemUiState(
    val foodItemId: Long? = null,

    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",

    val isEditMode: Boolean = false,

    val isSaving: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
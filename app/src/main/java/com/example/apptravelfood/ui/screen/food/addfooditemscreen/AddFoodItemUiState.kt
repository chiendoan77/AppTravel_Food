package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import android.net.Uri

data class AddFoodItemUiState(
    val foodItemId: Long? = null,

    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val localImageUri: Uri? = null,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
package com.example.apptravelfood.ui.screen.profilescreen

import com.example.apptravelfood.data.local.entity.UserEntity

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val phone: String = "",
    val error: String? = null,
    val success: String? = null,

    val totalCheckin: Int = 0,
    val totalFavorite: Int = 0,
    val totalFoodAdded: Int = 0,
)
package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.repository.FoodItemRepository

class AddFoodItemViewModelFactory(
    private val foodItemRepository: FoodItemRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFoodItemViewModel(foodItemRepository) as T
    }
}
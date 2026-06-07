package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.FoodItemRepository

class AddFoodItemViewModelFactory(
    private val foodItemRepository: FoodItemRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFoodItemViewModel(
            foodItemRepository = foodItemRepository,
            firebaseRepository = firebaseRepository
        ) as T
    }
}
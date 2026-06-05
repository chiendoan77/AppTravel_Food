package com.example.apptravelfood.ui.screen.food.addfoodstorescreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.repository.FoodStoreRepository

class AddFoodStoreViewModelFactory(
    private val foodStoreRepository: FoodStoreRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFoodStoreViewModel(foodStoreRepository) as T
    }
}
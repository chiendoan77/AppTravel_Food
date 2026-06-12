package com.example.apptravelfood.ui.screen.food.addfooditemscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.FoodItemRepository
import com.example.apptravelfood.data.repository.SupabaseStorageRepository

class AddFoodItemViewModelFactory(
    private val foodItemRepository: FoodItemRepository,
    private val firebaseRepository: FirebaseRepository,
    private val supabaseStorageRepository: SupabaseStorageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFoodItemViewModel(
            foodItemRepository = foodItemRepository,
            firebaseRepository = firebaseRepository,
            supabaseStorageRepository = supabaseStorageRepository
        ) as T
    }
}
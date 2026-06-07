package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.FoodItemRepository
import com.example.apptravelfood.data.repository.FoodStoreReviewRepository

class FoodStoreDetailViewModelFactory(
    private val foodItemRepository: FoodItemRepository,
    private val reviewRepository: FoodStoreReviewRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FoodStoreDetailViewModel(
            foodItemRepository = foodItemRepository,
            reviewRepository = reviewRepository,
            firebaseRepository = firebaseRepository
        ) as T
    }
}
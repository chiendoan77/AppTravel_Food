package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.FoodItemRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import com.example.apptravelfood.data.repository.FoodStoreReviewRepository
import com.example.apptravelfood.data.repository.UserRepository

class FoodStoreDetailViewModelFactory(
    private val foodItemRepository: FoodItemRepository,
    private val foodStoreRepository: FoodStoreRepository,
    private val reviewRepository: FoodStoreReviewRepository,
    private val firebaseRepository: FirebaseRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FoodStoreDetailViewModel(
            foodItemRepository = foodItemRepository,
            foodStoreRepository = foodStoreRepository,
            reviewRepository = reviewRepository,
            firebaseRepository = firebaseRepository,
            userRepository = userRepository
        ) as T
    }
}
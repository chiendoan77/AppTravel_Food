package com.example.apptravelfood.ui.screen.food.addfoodstorescreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.AddressRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository

class AddFoodStoreViewModelFactory(
    private val foodStoreRepository: FoodStoreRepository,
    private val firebaseRepository: FirebaseRepository,
    private val addressRepository: AddressRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFoodStoreViewModel(
            foodStoreRepository = foodStoreRepository,
            firebaseRepository = firebaseRepository,
            addressRepository = addressRepository
        ) as T
    }
}
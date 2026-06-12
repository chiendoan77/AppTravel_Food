package com.example.apptravelfood.ui.screen.food.addfoodstorescreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.AddressRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import com.example.apptravelfood.data.repository.SupabaseStorageRepository

class AddFoodStoreViewModelFactory(
    private val foodStoreRepository: FoodStoreRepository,
    private val firebaseRepository: FirebaseRepository,
    private val addressRepository: AddressRepository,
    private val supabaseStorageRepository: SupabaseStorageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFoodStoreViewModel(
            foodStoreRepository = foodStoreRepository,
            firebaseRepository = firebaseRepository,
            addressRepository = addressRepository,
            supabaseStorageRepository = supabaseStorageRepository
        ) as T
    }
}
package com.example.apptravelfood.ui.screen.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.FoodStoreRepository
import com.example.apptravelfood.data.repository.PlaceRepository
import com.example.apptravelfood.data.repository.PlaceRepositoryLocal

class HomeViewModelFactory(
    private val repository: PlaceRepository,
    private val placeRepositoryLocal: PlaceRepositoryLocal,
    private val foodStoreRepository: FoodStoreRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(
            repository = repository,
            placeRepositoryLocal = placeRepositoryLocal,
            foodStoreRepository = foodStoreRepository,
            firebaseRepository = firebaseRepository
        ) as T
    }
}
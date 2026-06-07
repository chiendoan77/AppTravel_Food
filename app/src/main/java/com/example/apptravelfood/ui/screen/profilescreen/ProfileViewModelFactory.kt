package com.example.apptravelfood.ui.screen.profilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.UserRepository

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(userRepository, firebaseRepository) as T
    }
}
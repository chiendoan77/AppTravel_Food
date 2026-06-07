package com.example.apptravelfood.ui.screen.authscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.SyncRepository
import com.example.apptravelfood.data.repository.UserRepository

class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val syncRepository: SyncRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            userRepository = userRepository,
            syncRepository = syncRepository,
            firebaseRepository = firebaseRepository
        ) as T
    }
}
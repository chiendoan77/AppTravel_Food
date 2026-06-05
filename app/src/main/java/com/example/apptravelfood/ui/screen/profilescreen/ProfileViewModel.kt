package com.example.apptravelfood.ui.screen.profilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val user = userRepository.getUser(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = user
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không tải được thông tin người dùng"
                )
            }
        }
    }

    fun updateName(userId: Long, name: String) {
        viewModelScope.launch {
            userRepository.updateName(userId, name)
            loadUser(userId)
        }
    }

    fun updateEmail(userId: Long, email: String) {
        viewModelScope.launch {
            userRepository.updateEmail(userId, email)
            loadUser(userId)
        }
    }

    fun updatePhone(userId: Long, phone: String) {
        viewModelScope.launch {
            userRepository.updatePhone(userId, phone)
            loadUser(userId)
        }
    }
}
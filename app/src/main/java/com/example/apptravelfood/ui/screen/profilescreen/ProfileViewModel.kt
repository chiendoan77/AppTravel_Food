package com.example.apptravelfood.ui.screen.profilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val firebaseRepository: FirebaseRepository
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
            try {
                userRepository.updateName(userId, name)
                backupUpdatedUser(userId)
                loadUser(userId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Không cập nhật được tên"
                )
            }
        }
    }

    fun updatePassword(userId: Long, password: String) {
        viewModelScope.launch {
            try {
                userRepository.updatePassword(userId, password)
                backupUpdatedUser(userId)
                loadUser(userId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Không đổi được mật khẩu"
                )
            }
        }
    }

    fun updatePhone(userId: Long, phone: String) {
        viewModelScope.launch {
            try {
                userRepository.updatePhone(userId, phone)
                backupUpdatedUser(userId)
                loadUser(userId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Không cập nhật được số điện thoại"
                )
            }
        }
    }

    fun updateBiometricEnabled(
        userId: Long,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            try {
                userRepository.updateBiometricEnabled(
                    userId = userId,
                    enabled = enabled
                )

                backupUpdatedUser(userId)
                loadUser(userId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Không cập nhật vân tay"
                )
            }
        }
    }

    private suspend fun backupUpdatedUser(userId: Long) {
        val updatedUser = userRepository.getUser(userId)

        if (updatedUser != null) {
            try {
                firebaseRepository.backupUser(updatedUser)
            } catch (_: Exception) {
                // Room đã cập nhật thành công.
                // Firebase lỗi thì không làm app crash.
            }
        }
    }
}
package com.example.apptravelfood.ui.screen.authscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.local.entity.UserEntity
import com.example.apptravelfood.data.repository.SyncRepository
import com.example.apptravelfood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val syncRepository: SyncRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun updateFullName(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    fun updatePhone(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isRegisterMode = !_uiState.value.isRegisterMode,
            error = null,
            registerSuccess = false
        )
    }

    fun login() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.value = state.copy(error = "Email là bắt buộc")
            return
        }

        viewModelScope.launch {
            var user = userRepository.getUserByEmail(state.email)

            if (user == null) {
                try {
                    val syncedUserId =
                        syncRepository.syncAfterLogin(state.email)

                    user = if (syncedUserId != null) {
                        userRepository.getUser(syncedUserId)
                    } else {
                        null
                    }
                } catch (_: Exception) {
                }
            }

            if (user == null) {
                _uiState.value = state.copy(
                    error = "Tài khoản chưa tồn tại. Hãy đăng ký."
                )
                return@launch
            }

            if (user.password.isNotBlank() && user.password != state.password) {
                _uiState.value = state.copy(
                    error = "Mật khẩu không đúng"
                )
                return@launch
            }

            try {
                syncRepository.syncAfterLogin(state.email)
            } catch (_: Exception) {
            }

            _uiState.value = state.copy(
                loggedUserId = user.userId,
                error = null
            )
        }
    }

    fun register() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.value = state.copy(error = "Email là bắt buộc")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(
                    isLoading = true,
                    error = null,
                    registerSuccess = false
                )

                val existedLocal = userRepository.getUserByEmail(state.email)

                if (existedLocal != null) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Email này đã được đăng ký"
                    )
                    return@launch
                }

                val existedFirebase = firebaseRepository.getUserByEmail(state.email)

                if (existedFirebase != null) {
                    userRepository.createUser(existedFirebase)

                    _uiState.value = AuthUiState(
                        email = state.email,
                        isRegisterMode = false,
                        registerSuccess = true,
                        error = null
                    )
                    return@launch
                }

                val newUserWithoutId = UserEntity(
                    fullName = state.fullName.ifBlank { "Người dùng TravelFood" },
                    email = state.email,
                    password = state.password,
                    phone = state.phone.ifBlank { null },
                    avatarUrl = null,
                    totalPoint = 0,
                    role = "USER"
                )

                val newUserId = userRepository.createUser(newUserWithoutId)

                val newUserWithId = newUserWithoutId.copy(
                    userId = newUserId
                )

                try {
                    firebaseRepository.backupUser(newUserWithId)
                } catch (e: Exception) {
                    // Không chặn đăng ký nếu Firebase lỗi
                }

                _uiState.value = AuthUiState(
                    email = state.email,
                    password = "",
                    fullName = "",
                    phone = "",
                    isRegisterMode = false,
                    registerSuccess = true,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "Đăng ký thất bại"
                )
            }
        }
    }

    fun loginWithBiometric(
        email: String,
        onNeedPassword: () -> Unit,
        onCanBiometric: (Long) -> Unit
    ) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Nhập email trước khi dùng vân tay"
            )
            return
        }

        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)

            if (user == null) {
                _uiState.value = _uiState.value.copy(
                    error = "Tài khoản chưa tồn tại"
                )
                return@launch
            }

            if (!user.biometricEnabled) {
                onNeedPassword()
                return@launch
            }

            onCanBiometric(user.userId)
        }
    }

    fun loginWithGoogle(
        email: String,
        fullName: String?,
        avatarUrl: String?
    ) {
        viewModelScope.launch {
            var user =
                userRepository.getUserByEmail(email)

            if (user == null) {
                val firebaseUser =
                    firebaseRepository.getUserByEmail(email)

                if (firebaseUser != null) {
                    userRepository.createUser(firebaseUser)
                    user = firebaseUser
                }
            }

            if (user != null) {
                try {
                    syncRepository.syncAfterLogin(email)
                } catch (_: Exception) {
                }

                _uiState.value = _uiState.value.copy(
                    loggedUserId = user.userId,
                    error = null
                )
                return@launch
            }

            val newUserWithoutId = UserEntity(
                fullName = fullName ?: "Người dùng Google",
                email = email,
                password = "",
                phone = null,
                avatarUrl = avatarUrl,
                totalPoint = 0,
                role = "USER"
            )

            val newUserId =
                userRepository.createUser(newUserWithoutId)

            val newUserWithId =
                newUserWithoutId.copy(userId = newUserId)

            try {
                firebaseRepository.backupUser(newUserWithId)
            } catch (_: Exception) {
            }

            _uiState.value = _uiState.value.copy(
                loggedUserId = newUserId,
                error = null
            )
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            error = message
        )
    }
}
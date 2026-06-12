package com.example.apptravelfood.ui.screen.profilescreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.core.untils.PasswordUtils
import com.example.apptravelfood.core.untils.ValidationUtils
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.repository.OtpRepository
import com.example.apptravelfood.data.repository.SupabaseStorageRepository
import com.example.apptravelfood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val firebaseRepository: FirebaseRepository,
    private val otpRepository: OtpRepository,
    private val supabaseStorageRepository: SupabaseStorageRepository
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
        if (!ValidationUtils.isValidPassword(password)) {
            _uiState.value = _uiState.value.copy(
                error = "Mật khẩu phải dài ít nhất 5 ký tự và có 1 chữ viết hoa"
            )
            return
        }
        viewModelScope.launch {
            try {
                userRepository.updatePassword(userId, PasswordUtils.hash(password))
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
        val state = _uiState.value

        if (state.phone.isNotBlank() && !ValidationUtils.isValidPhone(state.phone)) {
            _uiState.value = state.copy(
                error = "Số điện thoại phải có 10 số và bắt đầu bằng 0"
            )
            return
        }
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

    fun updateAvatar(
        context: Context,
        userId: Long,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val avatarUrl =
                    supabaseStorageRepository.uploadAvatar(
                        context = context,
                        userId = userId,
                        imageUri = imageUri
                    )
                Log.d("SUPABASE_UPLOAD", "Start uri=$imageUri")
                Log.d("SUPABASE_UPLOAD", "Uploaded avatar url=$avatarUrl")

                userRepository.updateAvatar(userId, avatarUrl)
                _uiState.value = _uiState.value.copy(
                    user = _uiState.value.user?.copy(
                        avatarUrl = avatarUrl
                    ),
                    isLoading = false,
                    error = "Cập nhật ảnh thành công"
                )

                backupUpdatedUser(userId)
                Log.d("SUPABASE_UPLOAD", "Avatar updated successfully")
                loadUser(userId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không cập nhật được ảnh"
                )
            }
        }
    }

    fun sendPasswordOtp(userId: Long) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(userId)

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Không tìm thấy tài khoản"
                    )
                    return@launch
                }

                if (user.authProvider == "GOOGLE") {
                    _uiState.value = _uiState.value.copy(
                        error = "Tài khoản Google không dùng mật khẩu app"
                    )
                    return@launch
                }

                otpRepository.sendOtp(user.email)
                Log.d("a", "success=\${response.success}, message=\${response.message}")

                _uiState.value = _uiState.value.copy(
                    error = "Đã gửi OTP về email"
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Không gửi được OTP"
                )
            }
        }
    }

    fun updatePasswordWithOtp(
        userId: Long,
        otp: String,
        newPassword: String
    ) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(userId)

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Không tìm thấy tài khoản"
                    )
                    return@launch
                }

                if (!ValidationUtils.isValidPassword(newPassword)) {
                    _uiState.value = _uiState.value.copy(
                        error = "Mật khẩu tối thiểu 5 ký tự và có ít nhất 1 chữ hoa"
                    )
                    return@launch
                }

                val newHash = PasswordUtils.hash(newPassword)

                val response = otpRepository.resetPassword(
                    email = user.email,
                    otp = otp,
                    newPasswordHash = newHash
                )

                if (!response.success) {
                    _uiState.value = _uiState.value.copy(
                        error = response.message
                    )
                    return@launch
                }

                val updatedUser = user.copy(
                    passwordHash = newHash
                )

                userRepository.insertUserReplace(updatedUser)
                firebaseRepository.backupUser(updatedUser)

                _uiState.value = _uiState.value.copy(
                    user = updatedUser,
                    error = "Đổi mật khẩu thành công"
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Đổi mật khẩu thất bại"
                )
            }
        }
    }
}
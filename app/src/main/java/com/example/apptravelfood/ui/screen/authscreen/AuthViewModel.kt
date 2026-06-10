package com.example.apptravelfood.ui.screen.authscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptravelfood.core.untils.PasswordUtils
import com.example.apptravelfood.core.untils.ValidationUtils
import com.example.apptravelfood.data.firebase.FirebaseAuthRepository
import com.example.apptravelfood.data.firebase.FirebaseRepository
import com.example.apptravelfood.data.local.entity.UserEntity
import com.example.apptravelfood.data.repository.OtpRepository
import com.example.apptravelfood.data.repository.SyncRepository
import com.example.apptravelfood.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val syncRepository: SyncRepository,
    private val firebaseRepository: FirebaseRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val otpRepository: OtpRepository
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

        if (state.password.isBlank()) {
            _uiState.value = state.copy(error = "Mật khẩu là bắt buộc")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                var user =
                    userRepository.getUserByEmail(state.email)

                if (user == null) {
                    val firebaseUser =
                        firebaseRepository.getUserByEmail(state.email)

                    if (firebaseUser != null) {
                        userRepository.insertUserReplace(firebaseUser)
                        user = firebaseUser
                    }
                }

                if (user == null) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Tài khoản chưa tồn tại. Hãy đăng ký."
                    )
                    return@launch
                }

                if (user.authProvider == "GOOGLE") {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Email này đăng nhập bằng Google. Vui lòng dùng Google."
                    )
                    return@launch
                }

                val inputHash = PasswordUtils.hash(state.password)

                if (user.passwordHash.isBlank()) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Tài khoản này đang dùng dữ liệu mật khẩu cũ. Hãy dùng Quên mật khẩu để đặt lại."
                    )
                    return@launch
                }

                if (user.passwordHash != inputHash) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Mật khẩu không đúng"
                    )
                    return@launch
                }

                val syncedUserId =
                    syncRepository.syncAfterLogin(state.email)

                _uiState.value = state.copy(
                    isLoading = false,
                    loggedUserId = syncedUserId ?: user.userId,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "Đăng nhập thất bại"
                )
            }
        }
    }

    fun register() {
        val state = _uiState.value

        if (!ValidationUtils.isValidEmail(state.email)) {
            _uiState.value = state.copy(
                error = "Email phải trên 5 ký tự và kết thúc bằng @gmail.com"
            )
            return
        }

        if (!ValidationUtils.isValidPassword(state.password)) {
            _uiState.value = state.copy(
                error = "Mật khẩu tối thiểu 5 ký tự và có ít nhất 1 chữ hoa"
            )
            return
        }

        if (state.phone.isNotBlank() && !ValidationUtils.isValidPhone(state.phone)) {
            _uiState.value = state.copy(
                error = "Số điện thoại phải có 10 số và bắt đầu bằng 0"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                val existedLocal =
                    userRepository.getUserByEmail(state.email)

                if (existedLocal != null) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Email này đã tồn tại. Vui lòng đăng nhập."
                    )
                    return@launch
                }

                val existedFirebase =
                    firebaseRepository.getUserByEmail(state.email)

                if (existedFirebase != null) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Email này đã được đăng ký trên hệ thống."
                    )
                    return@launch
                }

                val userWithoutId = UserEntity(
                    fullName = state.fullName.ifBlank { "Người dùng TravelFood" },
                    email = state.email,
                    phone = state.phone.ifBlank { null },
                    passwordHash = PasswordUtils.hash(state.password),
                    authProvider = "EMAIL",
                    emailVerified = false,
                    role = "USER",
                    totalPoint = 0
                )

                val newUserId =
                    userRepository.createUser(userWithoutId)

                val userWithId =
                    userWithoutId.copy(userId = newUserId)

                firebaseRepository.backupUser(userWithId)

                _uiState.value = AuthUiState(
                    email = state.email,
                    isRegisterMode = false,
                    registerSuccess = true,
                    error = "Đăng ký thành công. Hãy đăng nhập."
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
        idToken: String,
        email: String,
        fullName: String?,
        avatarUrl: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val firebaseUid =
                    firebaseAuthRepository.loginWithGoogle(idToken)

                var user =
                    userRepository.getUserByEmail(email)

                if (user == null) {
                    val firebaseUser =
                        firebaseRepository.getUserByEmail(email)

                    if (firebaseUser != null) {
                        userRepository.insertUserReplace(firebaseUser)
                        user = firebaseUser
                    }
                }

                if (user != null && user.authProvider == "EMAIL") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Email này đã được đăng ký bằng mật khẩu. Vui lòng đăng nhập bằng email/password."
                    )
                    return@launch
                }

                if (user == null) {
                    val userWithoutId = UserEntity(
                        firebaseUid = firebaseUid,
                        fullName = fullName ?: "Người dùng Google",
                        email = email,
                        phone = null,
                        passwordHash = "",
                        authProvider = "GOOGLE",
                        emailVerified = true,
                        avatarUrl = avatarUrl,
                        totalPoint = 0,
                        role = "USER"
                    )

                    val newUserId =
                        userRepository.createUser(userWithoutId)

                    user = userWithoutId.copy(
                        userId = newUserId
                    )

                    firebaseRepository.backupUser(user)
                }

                syncRepository.syncAfterLogin(email)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loggedUserId = user.userId,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Đăng nhập Google thất bại"
                )
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            error = message
        )
    }
    fun forgotPassword() {
        val email = _uiState.value.email

        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(
                error = "Nhập email @gmail.com hợp lệ trước"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val userLocal =
                    userRepository.getUserByEmail(email)

                val userFirebase =
                    firebaseRepository.getUserByEmail(email)

                val user = userLocal ?: userFirebase

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Email này chưa đăng ký tài khoản"
                    )
                    return@launch
                }

                if (user.authProvider == "GOOGLE") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Tài khoản Google không có mật khẩu app"
                    )
                    return@launch
                }

                val response =
                    otpRepository.sendOtp(email)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    forgotPasswordMode = true,
                    error = response.message
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không gửi được OTP"
                )
            }
        }
    }
    fun resetPasswordByOtp(
        otp: String,
        newPassword: String
    ) {
        val email = _uiState.value.email

        if (!ValidationUtils.isValidPassword(newPassword)) {
            _uiState.value = _uiState.value.copy(
                error = "Mật khẩu tối thiểu 5 ký tự và có ít nhất 1 chữ hoa"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val newPasswordHash =
                    PasswordUtils.hash(newPassword)

                val response =
                    otpRepository.resetPassword(
                        email = email,
                        otp = otp,
                        newPasswordHash = newPasswordHash
                    )

                if (!response.success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message
                    )
                    return@launch
                }

                var user =
                    userRepository.getUserByEmail(email)

                if (user == null) {
                    val firebaseUser =
                        firebaseRepository.getUserByEmail(email)

                    if (firebaseUser != null) {
                        userRepository.insertUserReplace(firebaseUser)
                        user = firebaseUser
                    }
                }

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Không tìm thấy tài khoản"
                    )
                    return@launch
                }

                val updatedUser =
                    user.copy(
                        passwordHash = newPasswordHash
                    )

                userRepository.insertUserReplace(updatedUser)
                firebaseRepository.backupUser(updatedUser)

                _uiState.value = AuthUiState(
                    email = email,
                    isRegisterMode = false,
                    error = "Đổi mật khẩu thành công. Hãy đăng nhập."
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Đổi mật khẩu thất bại"
                )
            }
        }
    }
    fun updateOtp(value: String) {
        _uiState.value = _uiState.value.copy(
            otp = value,
            error = null
        )
    }

    fun updateNewPassword(value: String) {
        _uiState.value = _uiState.value.copy(
            newPassword = value,
            error = null
        )
    }

    fun cancelForgotPassword() {
        _uiState.value = _uiState.value.copy(
            forgotPasswordMode = false,
            otp = "",
            newPassword = "",
            error = null
        )
    }
}
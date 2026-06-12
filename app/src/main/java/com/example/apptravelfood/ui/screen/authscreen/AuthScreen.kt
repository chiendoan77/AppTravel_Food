package com.example.apptravelfood.ui.screen.authscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.GMobiledata
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.apptravelfood.core.untils.BiometricHelper
import com.example.apptravelfood.core.untils.GoogleAuthHelper
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppAccentOutlinedButton
import com.example.apptravelfood.ui.components.AppSmallTag
import com.example.apptravelfood.ui.components.AppSurfaceSoft
import kotlinx.coroutines.launch

@Composable
fun AuthRoute(
    viewModel: AuthViewModel,
    onLoginSuccess: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val activity = context as FragmentActivity
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.loggedUserId) {
        uiState.loggedUserId?.let {
            onLoginSuccess(it)
        }
    }

    AuthScreen(
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onFullNameChange = viewModel::updateFullName,
        onPhoneChange = viewModel::updatePhone,
        onToggleMode = viewModel::toggleMode,
        onLoginClick = viewModel::login,
        onRegisterClick = viewModel::register,
        onBiometricClick = {
            viewModel.loginWithBiometric(
                email = uiState.email,
                onNeedPassword = {
                    viewModel.setError(
                        "Tài khoản này chưa bật đăng nhập vân tay. Vui lòng dùng mật khẩu."
                    )
                },
                onCanBiometric = { userId ->
                    BiometricHelper.showBiometricPrompt(
                        activity = activity,
                        onSuccess = {
                            onLoginSuccess(userId)
                        },
                        onError = {
                            viewModel.setError(it)
                        }
                    )
                }
            )
        },
        onGoogleLoginClick = {
            scope.launch {
                try {
                    val googleUser = GoogleAuthHelper.signIn(context)

                    viewModel.loginWithGoogle(
                        idToken = googleUser.idToken,
                        email = googleUser.email,
                        fullName = googleUser.fullName,
                        avatarUrl = googleUser.avatarUrl
                    )
                } catch (e: Exception) {
                    viewModel.setError(
                        e.message ?: "Đăng nhập Google thất bại"
                    )
                }
            }
        },
        onForgotPasswordClick = {
            viewModel.openForgotPassword()
        },
        onOtpChange = viewModel::updateOtp,
        onNewPasswordChange = viewModel::updateNewPassword,
        onResetPasswordClick = {
            viewModel.resetPasswordByOtp(
                otp = uiState.otp,
                newPassword = uiState.newPassword
            )
        },
        onCancelForgotPassword = viewModel::cancelForgotPassword,
        onSendForgotPasswordOtp = viewModel::sendForgotPasswordOtp
    )
}

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBiometricClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onOtpChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onResetPasswordClick: () -> Unit,
    onCancelForgotPassword: () -> Unit,
    onSendForgotPasswordOtp: () -> Unit,
) {
    AppPageSurface {
        val pagerState = rememberPagerState(pageCount = { 3 })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) { page ->
            when (page) {
                0 -> IntroPage(Icons.Default.Map, "Khám phá địa điểm", "Tìm khu du lịch, địa điểm nổi bật quanh bạn.")
                1 -> IntroPage(Icons.Default.FoodBank, "Gợi ý quán ăn", "Xem quán ăn do cộng đồng thêm gần địa điểm.")
                2 -> IntroPage(Icons.Default.Stars, "Check-in nhận điểm", "Điểm danh mỗi ngày và tích điểm trong app.")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppSurfaceSoft
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isRegisterMode) "Đăng ký" else "Đăng nhập",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppGreenStrong
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.isRegisterMode) {
                    OutlinedTextField(
                        value = uiState.fullName,
                        onValueChange = onFullNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tên hiển thị") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Số điện thoại") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email *") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mật khẩu") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
                if (!uiState.isRegisterMode) {
                    TextButton(
                        onClick = onForgotPasswordClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Quên mật khẩu?")
                    }
                }
                if (uiState.forgotPasswordMode) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "Quên mật khẩu",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (uiState.otpSent) {
                                    "OTP đã được gửi về email. Nhập OTP và mật khẩu mới."
                                } else {
                                    "Nhập email tài khoản của bạn rồi bấm Gửi OTP."
                                },
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = uiState.email,
                                onValueChange = onEmailChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Email nhận OTP") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, null)
                                },
                                singleLine = true,
                                enabled = !uiState.otpSent,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (!uiState.otpSent) {
                                AppAccentButton(
                                    text = if (uiState.isLoading) {
                                        "Đang gửi..."
                                    } else {
                                        "Gửi OTP"
                                    },
                                    onClick = onSendForgotPasswordOtp,
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !uiState.isLoading
                                )
                            }

                            if (uiState.otpSent) {
                                OutlinedTextField(
                                    value = uiState.otp,
                                    onValueChange = onOtpChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Mã OTP") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = uiState.newPassword,
                                    onValueChange = onNewPasswordChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Mật khẩu mới") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, null)
                                    },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                AppAccentButton(
                                    text = "Xác nhận đổi mật khẩu",
                                    onClick = onResetPasswordClick,
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !uiState.isLoading
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            AppAccentOutlinedButton(
                                text = "Hủy",
                                onClick = onCancelForgotPassword,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                if (uiState.registerSuccess) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Đăng ký thành công. Hãy đăng nhập.",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppAccentButton(
                    text = when {
                        uiState.isLoading -> "Đang xử lý..."
                        uiState.isRegisterMode -> "Tạo tài khoản"
                        else -> "Đăng nhập"
                    },
                    onClick = {
                        if (uiState.isRegisterMode) onRegisterClick() else onLoginClick()
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )

                if (!uiState.isRegisterMode) {
                    OutlinedButton(
                        onClick = onBiometricClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppGreen
                        ),
                        border = BorderStroke(1.dp, AppGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Vân tay")
                    }

                    OutlinedButton(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GMobiledata,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Tiếp tục với Google")
                    }
                }

                uiState.error?.let {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(
                    onClick = onToggleMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.isRegisterMode) {
                            "Đã có tài khoản? Đăng nhập"
                        } else {
                            "Chưa có tài khoản? Đăng ký"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IntroPage(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(58.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
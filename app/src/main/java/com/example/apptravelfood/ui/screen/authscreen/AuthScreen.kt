package com.example.apptravelfood.ui.screen.authscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.GMobiledata
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.apptravelfood.core.untils.BiometricHelper
import com.example.apptravelfood.core.untils.GoogleAuthHelper
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppAccentOutlinedButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
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
        // ── Branding Header ──────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Travel & Food",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(
                        colors = listOf(AppGreen, AppGreenStrong)
                    )
                )
            )
            Text(
                text = "Trải nghiệm ẩm thực và du lịch hoàn hảo",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Intro Pager ───────────────────────────────────────────
        val pagerState = rememberPagerState(pageCount = { 3 })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            when (page) {
                0 -> IntroPage(
                    Icons.Default.Map,
                    "Khám phá địa điểm",
                    "Tìm khu du lịch, địa điểm nổi bật quanh bạn."
                )

                1 -> IntroPage(
                    Icons.Default.FoodBank,
                    "Gợi ý quán ăn",
                    "Xem quán ăn do cộng đồng thêm gần địa điểm."
                )

                2 -> IntroPage(
                    Icons.Default.Stars,
                    "Check-in nhận điểm",
                    "Điểm danh mỗi ngày và tích điểm trong app."
                )
            }
        }

        // Pager dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { i ->
                val selected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (selected) AppGreen else AppGreenLight.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Auth Card ─────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurfaceSoft)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toggle tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppGreenLight.copy(alpha = 0.18f)),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Đăng nhập" to false, "Đăng ký" to true).forEach { (label, isRegister) ->
                        val active = uiState.isRegisterMode == isRegister
                        TextButton(
                            onClick = { if (uiState.isRegisterMode != isRegister) onToggleMode() },
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (active) Modifier
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AppGreen)
                                    else Modifier.padding(4.dp)
                                )
                        ) {
                            Text(
                                text = label,
                                color = if (active) Color.White else AppGreenStrong,
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── Register-only fields ──────────────────────────
                if (uiState.isRegisterMode) {
                    OutlinedTextField(
                        value = uiState.fullName,
                        onValueChange = onFullNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tên hiển thị") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = authFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Số điện thoại") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = authFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // ── Email ─────────────────────────────────────────
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, null, tint = AppGreen)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = authFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Password ──────────────────────────────────────
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mật khẩu") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null, tint = AppGreen)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = authFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                // Forgot password link
                if (!uiState.isRegisterMode) {
                    TextButton(
                        onClick = onForgotPasswordClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Quên mật khẩu?",
                            color = AppGreen,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // ── Forgot password panel ─────────────────────────
                if (uiState.forgotPasswordMode) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Quên mật khẩu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (uiState.otpSent)
                                    "OTP đã được gửi về email. Nhập OTP và mật khẩu mới."
                                else
                                    "Nhập email tài khoản của bạn rồi bấm Gửi OTP.",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = uiState.email,
                                onValueChange = onEmailChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Email nhận OTP") },
                                leadingIcon = { Icon(Icons.Default.Email, null, tint = AppGreen) },
                                singleLine = true,
                                enabled = !uiState.otpSent,
                                shape = RoundedCornerShape(12.dp),
                                colors = authFieldColors(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (!uiState.otpSent) {
                                AppAccentButton(
                                    text = if (uiState.isLoading) "Đang gửi..." else "Gửi OTP",
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
                                    shape = RoundedCornerShape(12.dp),
                                    colors = authFieldColors(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = uiState.newPassword,
                                    onValueChange = onNewPasswordChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Mật khẩu mới") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Lock,
                                            null,
                                            tint = AppGreen
                                        )
                                    },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = authFieldColors(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
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

                // Register success message
                if (uiState.registerSuccess) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Đăng ký thành công. Hãy đăng nhập.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Primary action row ────────────────────────────
                // Login mode: [Đăng nhập button] [Fingerprint icon button]
                // Register mode: [Tạo tài khoản full-width button]
                if (!uiState.isRegisterMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Main login button (takes remaining space)
                        Box(modifier = Modifier.weight(1f)) {
                            AppAccentButton(
                                text = if (uiState.isLoading) "Đang xử lý..." else "Đăng nhập",
                                onClick = onLoginClick,
                                enabled = !uiState.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            )
                        }

                        // Fingerprint icon button — ~48 dp (one finger tap target)
                        IconButton(
                            onClick = onBiometricClick,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AppGreenLight.copy(alpha = 0.22f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Đăng nhập vân tay",
                                tint = AppGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                } else {
                    AppAccentButton(
                        text = if (uiState.isLoading) "Đang xử lý..." else "Tạo tài khoản",
                        onClick = onRegisterClick,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }

                // ── Google sign-in (login mode only) ─────────────
                if (!uiState.isRegisterMode) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "  hoặc  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.GMobiledata,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = AppGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tiếp tục với Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // ── Error message ─────────────────────────────────
                uiState.error?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Toggle mode link — replaced by tab row above; keep as subtle fallback
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

// ── Shared field colour helper ──────────────────────────────────────────────
@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AppGreen,
    unfocusedBorderColor = AppGreenLight.copy(alpha = 0.5f),
    focusedLabelColor = AppGreen
)

// ── Intro slide ─────────────────────────────────────────────────────────────
@Composable
fun IntroPage(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppGreenLight.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AppGreenLight.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = AppGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppGreenStrong
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}
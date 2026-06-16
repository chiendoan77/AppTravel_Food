package com.example.apptravelfood.ui.screen.profilescreen.setting

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppAccentOutlinedButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppRed
import com.example.apptravelfood.ui.components.AppSurfaceSoft
import com.example.apptravelfood.ui.screen.profilescreen.ProfileUiState

@Composable
fun ProfileSettingScreen(
    uiState: ProfileUiState,
    onBack: () -> Unit,
    onSaveProfile: (String, String, Uri?) -> Unit,
    onUpdateBiometric: (Long, Boolean) -> Unit,
    onSendPasswordOtp: () -> Unit,
    onUpdatePasswordWithOtp: (String, String) -> Unit,
    onClearStatus: () -> Unit = {}
) {
    val context = LocalContext.current
    val user = uiState.user

    LaunchedEffect(uiState.success) {
        uiState.success?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            onClearStatus()
        }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            onClearStatus()
        }
    }

    var biometricEnabled by remember(user?.biometricEnabled) {
        mutableStateOf(
            user?.biometricEnabled ?: false
        )
    }
    var name by remember(user?.fullName) { mutableStateOf(user?.fullName ?: "") }
    var phone by remember(user?.phone) { mutableStateOf(user?.phone ?: "") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) selectedAvatarUri = uri }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Scrollable body ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            // Space for fixed top bar
            Spacer(modifier = Modifier.height(64.dp))

            // ── Avatar hero section ───────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppGreenLight.copy(alpha = 0.12f))
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, AppGreen, CircleShape)
                            .background(AppGreenLight.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedAvatarUri != null || user?.avatarUrl != null) {
                            AsyncImage(
                                model = selectedAvatarUri ?: user?.avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = name.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                color = AppGreenStrong
                            )
                        }
                    }

                    // Camera badge
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(30.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(AppGreen)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt, null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = user?.fullName ?: "Người dùng",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppGreenStrong
                )
                Text(
                    text = user?.email ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Personal info card ────────────────────────────────
            SettingSection(
                icon = Icons.Default.Person,
                title = "Thông tin cá nhân"
            ) {
                SettingTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Tên hiển thị",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Số điện thoại",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppAccentButton(
                    text = if (uiState.isLoading) "Đang lưu..." else "Lưu thay đổi",
                    onClick = { onSaveProfile(name, phone, selectedAvatarUri) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Security card ─────────────────────────────────────
            SettingSection(
                icon = Icons.Default.Security,
                title = "Bảo mật"
            ) {
                // Biometric toggle row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppGreenLight.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Fingerprint, null,
                                tint = AppGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Xác thực vân tay",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Đăng nhập nhanh bằng sinh trắc học",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = {
                            biometricEnabled = it
                            user?.userId?.let { uid -> onUpdateBiometric(uid, it) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AppGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(14.dp))

                // Change password
                if (!otpSent) {
                    AppAccentOutlinedButton(
                        text = "Đổi mật khẩu",
                        onClick = {
                            onSendPasswordOtp()
                            otpSent = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    SettingTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = "Mã OTP",
                        placeholder = "Nhập mã đã gửi về email",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mật khẩu mới",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppAccentButton(
                        text = "Cập nhật mật khẩu mới",
                        onClick = {
                            if (otp.isNotBlank() && password.isNotBlank()) {
                                onUpdatePasswordWithOtp(otp, password)
                                otp = ""
                                password = ""
                                otpSent = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    TextButton(
                        onClick = { otpSent = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hủy bỏ", color = AppRed, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // ── Fixed top bar ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .shadow(elevation = 2.dp)
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onClearStatus(); onBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppGreenLight.copy(alpha = 0.18f))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Quay lại",
                    tint = AppGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Cài đặt tài khoản",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppGreenStrong
            )
        }
    }
}

// ── Section wrapper card ──────────────────────────────────────────────────────
@Composable
private fun SettingSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AppGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppGreen,
                letterSpacing = 0.4.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurfaceSoft)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

// ── Text field ────────────────────────────────────────────────────────────────
@Composable
fun SettingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.LightGray) },
        leadingIcon = leadingIcon?.let {
            { Icon(it, null, tint = AppGreen, modifier = Modifier.size(20.dp)) }
        },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppGreen,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f),
            focusedLabelColor = AppGreen,
            cursorColor = AppGreen,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
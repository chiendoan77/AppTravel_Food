package com.example.apptravelfood.ui.screen.profilescreen.setting

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppAccentOutlinedButton
import com.example.apptravelfood.ui.components.AppGlassCard
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppRed
import com.example.apptravelfood.ui.screen.profilescreen.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
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
        mutableStateOf(user?.biometricEnabled ?: false)
    }

    var name by remember(user?.fullName) {
        mutableStateOf(user?.fullName ?: "")
    }

    var phone by remember(user?.phone) {
        mutableStateOf(user?.phone ?: "")
    }

    var password by remember {
        mutableStateOf("")
    }
    var otp by remember {
        mutableStateOf("")
    }

    var otpSent by remember {
        mutableStateOf(false)
    }
    var selectedAvatarUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                selectedAvatarUri = uri
            }
        }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cài đặt tài khoản",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onClearStatus()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, null, tint = AppGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->

        AppPageSurface(modifier = Modifier.padding(padding), scrollable = true) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                // Section 1: Profile Info
                Text(
                    text = "Thông tin cá nhân",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppGreenStrong,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                AppGlassCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = AppGreenLight,
                                border = androidx.compose.foundation.BorderStroke(2.dp, AppGreen)
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
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = name.take(1).uppercase(),
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = AppGreenStrong
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .background(AppGreen, CircleShape)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    SettingTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Tên hiển thị",
                        leadingIcon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Số điện thoại",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppAccentButton(
                        text = if (uiState.isLoading) "Đang lưu..." else "Lưu thay đổi",
                        onClick = {
                            onSaveProfile(name, phone, selectedAvatarUri)
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Security
                Text(
                    text = "Bảo mật",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppGreenStrong,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                AppGlassCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Xác thực vân tay",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Dùng sinh trắc học để đăng nhập nhanh",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = {
                                biometricEnabled = it
                                user?.userId?.let { userId -> onUpdateBiometric(userId, it) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AppGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(16.dp))

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
                            label = "Mã xác thực OTP",
                            placeholder = "Nhập mã đã gửi về email"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SettingTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Mật khẩu mới",
                            leadingIcon = Icons.Default.Lock,
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

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
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextButton(
                            onClick = { otpSent = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Hủy bỏ", color = AppRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SettingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        it,
                        null,
                        tint = AppGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppGreen,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

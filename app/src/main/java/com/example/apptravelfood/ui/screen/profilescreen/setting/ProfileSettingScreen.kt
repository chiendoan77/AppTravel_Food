package com.example.apptravelfood.ui.screen.profilescreen.setting

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.apptravelfood.ui.screen.profilescreen.ProfileUiState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingScreen(
    uiState: ProfileUiState,
    onBack: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdatePhone: (String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    onUpdateBiometric: (Long, Boolean) -> Unit,
    onAvatarSelected: (Uri) -> Unit,
    onSendPasswordOtp: () -> Unit,
    onUpdatePasswordWithOtp: (String, String) -> Unit,
) {
    val user = uiState.user
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
    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                onAvatarSelected(uri)
            }
        }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt tài khoản") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Thông tin cá nhân",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Chọn ảnh từ thư viện")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tên hiển thị") },
                        leadingIcon = { Icon(Icons.Default.Person, null) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Số điện thoại") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            user?.userId?.let {
                                onUpdateName(name)
                                onUpdatePhone(phone)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lưu thông tin")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Bảo mật",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            onSendPasswordOtp()
                            otpSent = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Gửi OTP đổi mật khẩu")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (otpSent) {
                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nhập OTP") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mật khẩu mới") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (otp.isNotBlank() && password.isNotBlank()) {
                                onUpdatePasswordWithOtp(
                                    otp,
                                    password
                                )
                                otp = ""
                                password = ""
                                otpSent = false
                            }
                        },
                        enabled = otpSent,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Lock, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xác nhận đổi mật khẩu")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (password.isNotBlank()) {
                                onUpdatePassword(password)
                                password = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Lock, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đổi mật khẩu")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Đăng nhập bằng vân tay",
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = "Bật để dùng sinh trắc học khi đăng nhập",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = {
                                biometricEnabled = it
                                user?.userId?.let { userId ->
                                    onUpdateBiometric(userId, it)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
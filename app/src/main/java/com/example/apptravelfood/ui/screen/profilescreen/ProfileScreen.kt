package com.example.apptravelfood.ui.screen.profilescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppSmallTag
import com.example.apptravelfood.ui.components.AppSurfaceSoft

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    userId: Long,
    onSettingClick: () -> Unit,
    onTermsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    ProfileScreen(
        uiState = uiState,
        onSettingClick = onSettingClick,
        onTermsClick = onTermsClick,
        onLogoutClick = onLogoutClick
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSettingClick: () -> Unit,
    onTermsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val user = uiState.user

    AppPageSurface(scrollable = true) {
        Text(
            text = "Tài khoản",
            style = MaterialTheme.typography.headlineSmall,
            color = AppGreenStrong
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppGreenLight
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(78.dp),
                        shape = CircleShape,
                        shadowElevation = 8.dp
                    ) {
                        AsyncImage(
                            model = user?.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = user?.fullName ?: "Chưa có tên",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AppSmallTag(text = "${user?.totalPoint ?: 0} điểm")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        ProfileInfoCard(
            email = user?.email ?: "Chưa có email",
            phone = user?.phone ?: "Chưa có số điện thoại"
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Tùy chọn",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileMenuItem(
            icon = Icons.Default.Settings,
            title = "Cài đặt",
            subtitle = "Đổi mật khẩu, số điện thoại, thông tin cá nhân",
            onClick = onSettingClick
        )

        ProfileMenuItem(
            icon = Icons.AutoMirrored.Filled.Assignment,
            title = "Điều khoản sử dụng",
            subtitle = "Chính sách và quy định ứng dụng",
            onClick = onTermsClick
        )

        ProfileMenuItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            title = "Đăng xuất",
            subtitle = "Thoát khỏi tài khoản hiện tại",
            danger = true,
            onClick = onLogoutClick
        )
    }
}

@Composable
fun ProfileInfoCard(
    email: String,
    phone: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppSurfaceSoft
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ProfileSmallInfo(
                icon = Icons.Default.Email,
                title = "Email",
                value = email
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            ProfileSmallInfo(
                icon = Icons.Default.Phone,
                title = "Số điện thoại",
                value = phone
            )
        }
    }
}

@Composable
fun ProfileSmallInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    danger: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = if (danger) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (danger) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (danger) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}
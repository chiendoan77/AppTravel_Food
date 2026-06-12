package com.example.apptravelfood.ui.screen.checkinscreen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiTransportation
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.apptravelfood.core.untils.BiometricHelper
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppSmallTag

@Composable
fun CheckinRoute(
    viewModel: CheckinViewModel,
    userId: Long
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as FragmentActivity

    var showPasswordDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(userId) {
        viewModel.loadCheckinData(userId)
    }

    CheckinScreen(
        uiState = uiState,
        showPasswordDialog = showPasswordDialog,

        onCheckinClick = {
            if (BiometricHelper.canAuthenticate(activity)) {
                BiometricHelper.showBiometricPrompt(
                    activity = activity,
                    onSuccess = {
                        viewModel.checkinToday(
                            userId = userId,
                            imageUrl = null,
                            faceVerified = true
                        )
                    },
                    onError = {
                        viewModel.showError(
                            "Xác thực sinh trắc học thất bại"
                        )
                    }
                )
            } else {
                showPasswordDialog = true
            }
        },

        onPasswordCheckClick = { password ->
            showPasswordDialog = false

            viewModel.checkinWithPassword(
                userId = userId,
                password = password
            )
        },

        onDismissPasswordDialog = {
            showPasswordDialog = false
        }
    )
}

@Composable
fun CheckinScreen(
    uiState: CheckinUiState,
    showPasswordDialog: Boolean,
    onCheckinClick: () -> Unit,
    onPasswordCheckClick: (String) -> Unit,
    onDismissPasswordDialog: () -> Unit
) {

    AppPageSurface {
        Text(
            text = "Tích điểm nhận quà",
            style = MaterialTheme.typography.headlineSmall,
            color = AppGreenStrong
        )

        Spacer(modifier = Modifier.height(16.dp))

        TotalPointCard(
            totalPoint = uiState.totalPoint
        )

        Spacer(modifier = Modifier.height(18.dp))

        BusTicketCheckinCard(
            checkedDays = uiState.checkedDays,
            hasCheckedToday = uiState.hasCheckedToday
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppAccentButton(
            text = if (uiState.hasCheckedToday) "Hôm nay đã điểm danh" else "Điểm danh hôm nay",
            onClick = onCheckinClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.hasCheckedToday && !uiState.isLoading
        )

        uiState.message?.let {
            Spacer(modifier = Modifier.height(12.dp))
            AppSmallTag(text = it)
        }

        uiState.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
        if (showPasswordDialog) {
            var password by remember {
                mutableStateOf("")
            }

            AlertDialog(
                onDismissRequest = onDismissPasswordDialog,
                title = {
                    Text("Xác thực điểm danh")
                },
                text = {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = {
                            Text("Nhập mật khẩu tài khoản")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onPasswordCheckClick(password)
                        },
                        enabled = password.isNotBlank()
                    ) {
                        Text("Xác nhận")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismissPasswordDialog
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
fun TotalPointCard(
    totalPoint: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(68.dp),
                shape = CircleShape,
                color = AppGreenLight
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Text(
                    text = "Tổng điểm của bạn",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "$totalPoint điểm",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppGreen
                )
            }
        }
    }
}

@Composable
fun BusTicketCheckinCard(
    checkedDays: List<Int>,
    hasCheckedToday: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.EmojiTransportation, contentDescription = null)

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Vé điểm danh 7 ngày",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Mỗi ngày điểm danh nhận điểm. Ô đã nhận sẽ được đóng dấu.",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (day in 1..4) {
                        CheckinWindow(
                            day = day,
                            point = getPointByDay(day),
                            checked = checkedDays.contains(day),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (day in 5..7) {
                        CheckinWindow(
                            day = day,
                            point = getPointByDay(day),
                            checked = checkedDays.contains(day),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (hasCheckedToday) {
                Text(
                    text = "Hôm nay bạn đã lên xe và nhận điểm 🎫",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Hôm nay chưa điểm danh",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CheckinWindow(
    day: Int,
    point: Int,
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (checked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ngày $day",
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "+$point",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (checked) "Đã nhận" else "Chờ",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

fun getPointByDay(day: Int): Int {
    return when (day) {
        1 -> 5
        2 -> 5
        3 -> 10
        4 -> 10
        5 -> 15
        6 -> 15
        7 -> 30
        else -> 0
    }
}
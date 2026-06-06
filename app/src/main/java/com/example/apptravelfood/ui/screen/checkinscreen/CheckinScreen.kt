package com.example.apptravelfood.ui.screen.checkinscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiTransportation
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.apptravelfood.core.untils.BiometricHelper

@Composable
fun CheckinRoute(
    viewModel: CheckinViewModel,
    userId: Long
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as FragmentActivity

    LaunchedEffect(userId) {
        viewModel.loadCheckinData(userId)
    }

    CheckinScreen(
        uiState = uiState,
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
                        viewModel.checkinToday(
                            userId = userId,
                            imageUrl = null,
                            faceVerified = false
                        )
                    }
                )
            }
        }
    )
}

@Composable
fun CheckinScreen(
    uiState: CheckinUiState,
    onCheckinClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Tích điểm nhận quà",
            style = MaterialTheme.typography.headlineSmall
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

        Button(
            onClick = onCheckinClick,
            enabled = !uiState.hasCheckedToday && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (uiState.hasCheckedToday) {
                    "Hôm nay đã điểm danh"
                } else {
                    "Điểm danh hôm nay"
                }
            )
        }

        uiState.message?.let {
            Spacer(modifier = Modifier.height(12.dp))
            AssistChip(
                onClick = {},
                label = { Text(it) },
                leadingIcon = {
                    Icon(Icons.Default.Stars, contentDescription = null)
                }
            )
        }

        uiState.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
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
                color = MaterialTheme.colorScheme.primaryContainer
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
                    color = MaterialTheme.colorScheme.primary
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
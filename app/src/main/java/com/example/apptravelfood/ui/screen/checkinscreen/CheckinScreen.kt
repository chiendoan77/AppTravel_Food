package com.example.apptravelfood.ui.screen.checkinscreen

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.apptravelfood.core.untils.BiometricHelper
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppSmallTag
import com.example.apptravelfood.ui.components.AppSurfaceSoft

@Composable
fun CheckinRoute(
    viewModel: CheckinViewModel,
    userId: Long
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as FragmentActivity

    var showPasswordDialog by remember { mutableStateOf(false) }

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
                        viewModel.showError("Xác thực sinh trắc học thất bại")
                    }
                )
            } else {
                showPasswordDialog = true
            }
        },
        onPasswordCheckClick = { password ->
            showPasswordDialog = false
            viewModel.checkinWithPassword(userId = userId, password = password)
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
        // Branding Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Travel & Food",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.linearGradient(
                        colors = listOf(AppGreen, AppGreenStrong)
                    )
                )
            )
            Text(
                text = "Check-in hàng ngày, nhận quà liền tay",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tích điểm nhận quà",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = AppGreenStrong
        )

        Spacer(modifier = Modifier.height(12.dp))

        TotalPointCard(totalPoint = uiState.totalPoint)

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
                .height(52.dp),
            enabled = !uiState.hasCheckedToday && !uiState.isLoading
        )

        uiState.message?.let {
            Spacer(modifier = Modifier.height(12.dp))
            AppSmallTag(text = it)
        }

        uiState.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (showPasswordDialog) {
            var password by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = onDismissPasswordDialog,
                title = { Text("Xác thực điểm danh") },
                text = {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Nhập mật khẩu tài khoản") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { onPasswordCheckClick(password) },
                        enabled = password.isNotBlank()
                    ) { Text("Xác nhận") }
                },
                dismissButton = {
                    TextButton(onClick = onDismissPasswordDialog) { Text("Hủy") }
                }
            )
        }
    }
}

// ── Total point card ──────────────────────────────────────────────────────────
@Composable
fun TotalPointCard(totalPoint: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = AppGreenLight.copy(alpha = 0.35f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = AppGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Tổng điểm của bạn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "$totalPoint điểm",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = AppGreen
                )
            }
        }
    }
}

// ── 7-day checkin card ────────────────────────────────────────────────────────
@Composable
fun BusTicketCheckinCard(
    checkedDays: List<Int>,
    hasCheckedToday: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceSoft)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.EmojiTransportation,
                    contentDescription = null,
                    tint = AppGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Vé điểm danh 7 ngày",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppGreenStrong
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Điểm danh mỗi ngày để nhận điểm. Ô đã nhận sẽ bị gạch.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // All 7 days in one row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (day in 1..7) {
                    CheckinCell(
                        day = day,
                        point = getPointByDay(day),
                        checked = checkedDays.contains(day),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (hasCheckedToday) "✅ Hôm nay đã điểm danh, nhận điểm thành công!"
                else "Hôm nay chưa điểm danh",
                style = MaterialTheme.typography.bodySmall,
                color = if (hasCheckedToday) AppGreen else Color.Gray
            )
        }
    }
}

// ── Single day cell ───────────────────────────────────────────────────────────
@Composable
fun CheckinCell(
    day: Int,
    point: Int,
    checked: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (checked) Color(0xFFFFF0F0) else Color.White
    val borderColor = if (checked) Color(0xFFFFCCCC) else AppGreenLight.copy(alpha = 0.4f)
    val textColor = if (checked) Color(0xFFBB4444) else AppGreenStrong

    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = "N$day",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "+$point",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (checked) "✓" else "…",
                fontSize = 9.sp,
                color = if (checked) Color(0xFFCC2222) else Color.LightGray,
                textAlign = TextAlign.Center
            )
        }

        // Red diagonal cross drawn on top when checked
        if (checked) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 2.dp.toPx()
                val color = Color(0xFFDD3333)
                val pad = 8.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(pad, pad),
                    end = Offset(size.width - pad, size.height - pad),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(size.width - pad, pad),
                    end = Offset(pad, size.height - pad),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

fun getPointByDay(day: Int): Int = when (day) {
    1 -> 5
    2 -> 5
    3 -> 10
    4 -> 10
    5 -> 15
    6 -> 15
    7 -> 30
    else -> 0
}
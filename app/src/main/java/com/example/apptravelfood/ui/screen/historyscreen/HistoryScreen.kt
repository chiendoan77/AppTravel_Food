package com.example.apptravelfood.ui.screen.historyscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppSmallTag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryRoute(
    viewModel: HistoryViewModel,
    userId: Long
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadHistory(userId)
    }

    HistoryScreen(uiState = uiState)
}

@Composable
fun HistoryScreen(
    uiState: HistoryUiState
) {
    var selectedTab by remember { mutableStateOf(0) }

    AppPageSurface {
        Text(
            text = "Lịch sử hoạt động",
            style = MaterialTheme.typography.headlineSmall,
            color = AppGreenStrong
        )

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Check-in") }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Điểm") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            selectedTab == 0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.checkins) { item ->
                        HistoryCheckinItem(
                            time = item.checkinTime,
                            point = item.pointEarned,
                            faceVerified = item.faceVerified
                        )
                    }
                }
            }

            selectedTab == 1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.pointHistories) { item ->
                        HistoryPointItem(
                            type = item.type,
                            point = item.point,
                            description = item.description ?: "",
                            time = item.createdAt
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCheckinItem(
    time: Long,
    point: Int,
    faceVerified: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Check-in hằng ngày",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatHistoryTime(time),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (faceVerified) {
                        "Xác thực khuôn mặt thành công"
                    } else {
                        "Chưa xác thực khuôn mặt"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AppSmallTag(text = "+$point")
        }
    }
}

@Composable
fun HistoryPointItem(
    type: String,
    point: Int,
    description: String,
    time: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(Icons.Default.Stars, contentDescription = null)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = type,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = formatHistoryTime(time),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AppSmallTag(text = if (point >= 0) "+$point" else "$point")
        }
    }
}

fun formatHistoryTime(time: Long): String {
    return SimpleDateFormat(
        "HH:mm - dd/MM/yyyy",
        Locale.forLanguageTag("vi-VN")
    ).format(Date(time))
}
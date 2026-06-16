package com.example.apptravelfood.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyReviewEditor(
    rating: Float,
    comment: String,
    hasReview: Boolean,
    onRatingChange: (Float) -> Unit,
    onCommentChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceSoft)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header ────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = AppGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (hasReview) "Đánh giá của bạn" else "Viết đánh giá",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AppGreenStrong
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Star selector ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppGreenLight.copy(alpha = 0.12f))
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (rating.toInt()) {
                        1 -> "Rất tệ"
                        2 -> "Tệ"
                        3 -> "Bình thường"
                        4 -> "Tốt"
                        5 -> "Xuất sắc ✨"
                        else -> "Chưa chọn sao"
                    },
                    fontSize = 12.sp,
                    color = if (rating > 0) AppGreenStrong else Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (star in 1..5) {
                        val filled = star <= rating.toInt()

                        val starColor by animateColorAsState(
                            targetValue = if (filled) Color(0xFFFFC107) else Color(0xFFDDDDDD),
                            animationSpec = spring(stiffness = Spring.StiffnessMedium),
                            label = "starColor$star"
                        )
                        val starScale by animateFloatAsState(
                            targetValue = if (filled) 1.18f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "starScale$star"
                        )

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Sao $star",
                            tint = starColor,
                            modifier = Modifier
                                .size(36.dp)
                                .scale(starScale)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onRatingChange(star.toFloat()) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Comment field ─────────────────────────────────────
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nhận xét của bạn") },
                placeholder = {
                    Text("Chia sẻ trải nghiệm về quán...", color = Color.LightGray)
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGreen,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f),
                    focusedLabelColor = AppGreen,
                    cursorColor = AppGreen
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Action buttons ────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AppAccentButton(
                    text = if (hasReview) "Cập nhật" else "Gửi đánh giá",
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f)
                )
                if (hasReview) {
                    AppAccentOutlinedButton(
                        text = "Xóa",
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(0.45f),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // ── Delete dialog ─────────────────────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa đánh giá", fontWeight = FontWeight.SemiBold) },
            text = { Text("Bạn có chắc muốn xóa đánh giá này không?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDeleteClick() }) {
                    Text(
                        "Xóa",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
package com.example.apptravelfood.ui.components

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = if (hasReview) {
                    "Bạn đã đánh giá quán này"
                } else {
                    "Viết đánh giá của bạn"
                },
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Số sao:")

                Spacer(modifier = Modifier.width(10.dp))

                for (star in 1..5) {
                    IconButton(
                        onClick = {
                            onRatingChange(star.toFloat())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= rating.toInt()) AppGreen else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Bình luận")
                },
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppAccentButton(
                    text = if (hasReview) "Cập nhật" else "Gửi đánh giá",
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f)
                )

                if (hasReview) {
                    AppAccentOutlinedButton(
                        text = "Xóa",
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Xóa đánh giá")
            },
            text = {
                Text("Bạn có chắc muốn xóa đánh giá này không?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text(
                        text = "Xóa",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}
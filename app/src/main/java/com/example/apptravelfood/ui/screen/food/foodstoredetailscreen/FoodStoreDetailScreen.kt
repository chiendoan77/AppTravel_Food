package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppSmallTag
import com.example.apptravelfood.ui.components.MyReviewEditor

@Composable
fun FoodStoreDetailRoute(
    viewModel: FoodStoreDetailViewModel,
    store: FoodStoreEntity,
    userId: Long,
    onBack: () -> Unit,
    onAddFoodItemClick: (FoodStoreEntity) -> Unit,
    onFoodItemClick: (FoodItemEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(store.foodStoreId, userId) {
        viewModel.loadStoreDetail(
            store = store,
            userId = userId
        )
    }

    FoodStoreDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onAddFoodItemClick = onAddFoodItemClick,
        onFoodItemClick = onFoodItemClick,
        onRatingChange = viewModel::updateRating,
        onCommentChange = viewModel::updateComment,
        onSaveReviewClick = {
            viewModel.saveReview(userId)
        },
        onDeleteReviewClick = {
            viewModel.deleteMyReview(userId)
        }
    )
}

@Composable
fun FoodStoreDetailScreen(
    uiState: FoodStoreDetailUiState,
    onBack: () -> Unit,
    onAddFoodItemClick: (FoodStoreEntity) -> Unit,
    onFoodItemClick: (FoodItemEntity) -> Unit,
    onRatingChange: (Float) -> Unit,
    onCommentChange: (String) -> Unit,
    onSaveReviewClick: () -> Unit,
    onDeleteReviewClick: () -> Unit
) {
    val store = uiState.store

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box {
                AsyncImage(
                    model = store?.imageUrl,
                    contentDescription = store?.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    contentScale = ContentScale.Crop
                )

                // back button in white circular surface for better contrast
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier
                        .padding(12.dp)
                        .size(40.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Quay lại",
                            tint = AppGreen
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = store?.name ?: "Chi tiết quán ăn",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppGreenStrong
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = store?.address ?: "Chưa có địa chỉ",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppSmallTag(text = "${uiState.averageRating} ★")
                    AppSmallTag(text = "${uiState.reviewCount} đánh giá")
                }

                store?.description?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (uiState.isOwner) {
                    Spacer(modifier = Modifier.height(10.dp))

                    AppSmallTag(text = "Bạn là người đóng góp quán này")
                }

                Spacer(modifier = Modifier.height(18.dp))

                SectionTitle(
                    icon = Icons.Default.RestaurantMenu,
                    title = "Menu món ăn"
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.isOwner) {
                    store?.let {
                        AppAccentButton(
                            text = "＋ Thêm món ăn",
                            onClick = { onAddFoodItemClick(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (uiState.foodItems.isEmpty()) {
            item {
                Text(
                    text = "Quán này chưa có món ăn",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(uiState.foodItems) { food ->
                FoodItemCard(
                    food = food,
                    canEdit = uiState.isOwner,
                    onClick = {
                        if (uiState.isOwner) {
                            onFoodItemClick(food)
                        }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                SectionTitle(
                    icon = Icons.Default.Star,
                    title = "Đánh giá của bạn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                MyReviewEditor(
                    rating = uiState.ratingInput,
                    comment = uiState.commentInput,
                    hasReview = uiState.myReview != null,
                    onRatingChange = onRatingChange,
                    onCommentChange = onCommentChange,
                    onSaveClick = onSaveReviewClick,
                    onDeleteClick = onDeleteReviewClick
                )

                uiState.message?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                SectionTitle(
                    icon = Icons.Default.CommentBank,
                    title = "Bình luận cộng đồng"
                )
            }
        }

        if (uiState.reviews.isEmpty()) {
            item {
                Text(
                    text = "Chưa có bình luận nào",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            items(uiState.reviews) { review ->
                ReviewItem(
                    rating = review.rating,
                    comment = review.comment
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FoodItemCard(
    food: FoodItemEntity,
    canEdit: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = food.description ?: "Chưa có mô tả",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${food.price ?: 0.0} đ",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppGreen
                )

                if (canEdit) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Nhấn để sửa món",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    rating: Float,
    comment: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = AppGreenLight
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "U",
                        style = MaterialTheme.typography.titleSmall,
                        color = AppGreenStrong
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppSmallTag(text = "$rating ★")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comment,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
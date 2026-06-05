package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.ui.components.MyReviewEditor

@Composable
fun FoodStoreDetailRoute(
    viewModel: FoodStoreDetailViewModel,
    store: FoodStoreEntity,
    userId: Long,
    onBack: () -> Unit,
    onAddFoodItemClick: (FoodStoreEntity) -> Unit,
    onFoodItemClick: (FoodItemEntity) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(store.foodStoreId) {
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
    onRatingChange: (Float) -> Unit,
    onCommentChange: (String) -> Unit,
    onSaveReviewClick: () -> Unit,
    onDeleteReviewClick: () -> Unit,
    onFoodItemClick: (FoodItemEntity) -> Unit,

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

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Quay lại")
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = store?.name ?: "Chi tiết quán ăn",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = store?.address ?: "Chưa có địa chỉ",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                AssistChip(
                    onClick = {},
                    label = {
                        Text("${uiState.averageRating} sao • ${uiState.reviewCount} đánh giá")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Star, contentDescription = null)
                    }
                )

                store?.description?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(
                    icon = Icons.Default.RestaurantMenu,
                    title = "Menu món ăn"
                )
                Spacer(modifier = Modifier.height(12.dp))

                store?.let {
                    Button(
                        onClick = {
                            onAddFoodItemClick(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("＋ Thêm món ăn")
                    }
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
                Card(
                    onClick = {
                        onFoodItemClick(food)
                    },
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
                            modifier = Modifier.size(82.dp),
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
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                SectionTitle(
                    icon = Icons.Default.CommentBank,
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
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("U")
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text("${review.rating} sao")
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = review.comment,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
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
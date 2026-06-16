package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.ui.components.AppAccentButton
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenLight
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppSurfaceSoft
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
        viewModel.loadStoreDetail(store = store, userId = userId)
    }

    FoodStoreDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onAddFoodItemClick = onAddFoodItemClick,
        onFoodItemClick = onFoodItemClick,
        onRatingChange = viewModel::updateRating,
        onCommentChange = viewModel::updateComment,
        onSaveReviewClick = { viewModel.saveReview(userId) },
        onDeleteReviewClick = { viewModel.deleteMyReview(userId) },
        onDeleteStoreClick = { viewModel.deleteFoodStore(onSuccess = onBack) }
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
    onDeleteReviewClick: () -> Unit,
    onDeleteStoreClick: () -> Unit
) {
    val store = uiState.store
    val context = LocalContext.current
    val listState = rememberLazyListState()

    // Show solid top bar once user scrolls past hero image (~260dp)
    val showSolidBar by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Scrollable content ────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {

            // Hero image — no top padding, sits behind status bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = store?.imageUrl,
                        contentDescription = store?.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient: dark top (status bar area) → transparent → dark bottom
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Black.copy(alpha = 0.35f),
                                    0.35f to Color.Transparent,
                                    1f to Color.Black.copy(alpha = 0.55f)
                                )
                            )
                    )

                    // Rating / review pills at bottom of hero
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoPill {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${uiState.averageRating}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        InfoPill {
                            Text(
                                "${uiState.reviewCount} đánh giá",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // ── Store info ────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {

                    Text(
                        text = store?.name ?: "Chi tiết quán ăn",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = AppGreenStrong
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            tint = AppGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = store?.address ?: "Chưa có địa chỉ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    store?.description?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }

                    if (store?.latitude != null && store.longitude != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AppAccentButton(
                            text = "Xem trên bản đồ 📍",
                            onClick = {
                                val gmmIntentUri =
                                    "google.navigation:q=${store.latitude},${store.longitude}".toUri()
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        )
                    }

                    if (uiState.isOwner) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFF0F0))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Bạn là người đóng góp quán này",
                                fontSize = 13.sp,
                                color = Color(0xFFCC3333)
                            )
                            IconButton(
                                onClick = onDeleteStoreClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = Color(0xFFCC3333),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // ── Menu ─────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    SectionTitle(icon = Icons.Default.RestaurantMenu, title = "Menu món ăn")

                    if (uiState.isOwner) {
                        Spacer(modifier = Modifier.height(10.dp))
                        store?.let {
                            AppAccentButton(
                                text = "＋ Thêm món ăn",
                                onClick = { onAddFoodItemClick(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            )
                        }
                    }
                }
            }

            if (uiState.foodItems.isEmpty()) {
                item {
                    Text(
                        text = "Quán này chưa có món ăn nào.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                items(uiState.foodItems) { food ->
                    FoodItemCard(
                        food = food,
                        canEdit = uiState.isOwner,
                        onClick = { if (uiState.isOwner) onFoodItemClick(food) }
                    )
                }
            }

            // ── My review ─────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionTitle(icon = Icons.Default.Star, title = "Đánh giá của bạn")
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
                            it,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    uiState.error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionTitle(icon = Icons.Default.CommentBank, title = "Bình luận cộng đồng")
                }
            }

            if (uiState.reviews.isEmpty()) {
                item {
                    Text(
                        text = "Chưa có bình luận nào.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                items(uiState.reviews) { review ->
                    val user = uiState.reviewUsers[review.userId]
                    ReviewItem(
                        userName = user?.fullName ?: "Người dùng",
                        avatarUrl = user?.avatarUrl,
                        rating = review.rating,
                        comment = review.comment
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }

        // ── Sticky top bar — always visible, changes style on scroll ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (showSolidBar)
                        Modifier
                            .shadow(4.dp)
                            .background(Color.White)
                    else
                        Modifier.background(Color.Transparent)
                )
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(if (showSolidBar) 0.dp else 3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (showSolidBar) AppGreenLight.copy(alpha = 0.18f)
                        else Color.White.copy(alpha = 0.92f)
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Quay lại",
                    tint = AppGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Show store name in bar only after scrolling past hero
            if (showSolidBar) {
                Text(
                    text = store?.name ?: "",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppGreenStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 52.dp)
                )
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun InfoPill(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = { content() }
    )
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
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AppGreenStrong,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = food.description ?: "Chưa có mô tả",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${food.price ?: 0.0}đ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppGreen
                    )
                    if (canEdit) {
                        Text("Chạm để sửa", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    userName: String,
    avatarUrl: String?,
    rating: Float,
    comment: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceSoft)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppGreenLight.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl != null) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = userName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = AppGreenStrong
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = AppGreenStrong
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "$rating",
                            fontSize = 12.sp,
                            color = Color(0xFF8B6914),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun SectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = AppGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = AppGreenStrong
        )
    }
}
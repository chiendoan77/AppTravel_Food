package com.example.apptravelfood.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.core.untils.DistanceUtils
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.remote.dto.LocalResultsDto

@Composable
fun PlaceItem(
    place: LocalResultsDto,
    foodStores: List<FoodStoreEntity>,
    onClick: () -> Unit,
    onAddFoodStoreClick: (LocalResultsDto) -> Unit,
    onFoodStoreClick: (FoodStoreEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppSurfaceSoft
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                AsyncImage(
                    model = place.thumbnail_large,
                    contentDescription = place.title,
                    modifier = Modifier
                        .weight(0.35f)
                        .aspectRatio(1f)
                        .clip(AppImageShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(0.65f)
                ) {
                    Text(
                        text = place.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = place.address ?: "Chưa có địa chỉ",
                        maxLines = 2,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppSmallTag(text = "${place.rating ?: 0.0} ★")
                        AppSmallTag(text = place.type ?: "Địa điểm")
                    }
                }
            }

            Divider(
                modifier = Modifier.padding(horizontal = 14.dp),
                thickness = 1.dp,
                color = Color(0xFFE6ECE4)
            )

            Spacer(modifier = Modifier.height(10.dp))

            AppAccentButton(
                text = "＋ Thêm quán ăn gần địa điểm này",
                onClick = { onAddFoodStoreClick(place) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = AppGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quán ăn gần đây",
                        style = MaterialTheme.typography.titleSmall,
                        color = AppGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (foodStores.isEmpty()) {
                    Text(
                        text = "Chưa có quán ăn nào trong khu vực này.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(foodStores) { store ->
                            val distanceKm = DistanceUtils.calculateDistanceKm(
                                startLat = place.gps_coordinates?.latitude,
                                startLng = place.gps_coordinates?.longitude,
                                endLat = store.latitude,
                                endLng = store.longitude
                            )

                            FoodStoreMiniCard(
                                store = store,
                                distance = DistanceUtils.formatDistance(distanceKm),
                                onClick = {
                                    onFoodStoreClick(store)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodStoreMiniCard(
    store: FoodStoreEntity,
    distance: String = "",
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(240.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppSurfaceSoft
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = store.imageUrl,
                contentDescription = store.name,
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .clip(AppImageShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(vertical = 10.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = AppGreen,
                    maxLines = 2
                )

                if (distance.isNotBlank()) {
                    Text(
                        text = "📍 Cách $distance",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppGreenStrong
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "⭐ 4.8",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFC107)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = store.address ?: "Chưa có địa chỉ",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = store.description ?: "Xem menu món ăn",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}
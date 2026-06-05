package com.example.apptravelfood.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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
            .padding(8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp)
            ) {
                AsyncImage(
                    model = place.thumbnail,
                    contentDescription = place.title,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = place.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        Icon(Icons.Default.LocationOn, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.address ?: "Chưa có địa chỉ",
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        Icon(Icons.Default.Star, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.rating?.toString()
                                ?: "Chưa có đánh giá"
                        )
                    }
                }
            }

            HorizontalDivider()
            Button(
                onClick = {
                    onAddFoodStoreClick(place)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("＋ Thêm quán ăn gần địa điểm này")
            }
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row {
                    Icon(Icons.Default.Store, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quán ăn gần đây",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (foodStores.isEmpty()) {
                    Text(
                        text = "Chưa có quán ăn nào được thêm",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(foodStores) { store ->
                            FoodStoreMiniCard(
                                store = store,
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
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(170.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = store.imageUrl,
                contentDescription = store.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = store.address ?: "Chưa có địa chỉ",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = store.description ?: "Xem menu món ăn",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}
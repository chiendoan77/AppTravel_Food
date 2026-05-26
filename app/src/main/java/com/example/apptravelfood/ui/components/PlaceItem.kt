package com.example.apptravelfood.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.remote.dto.LocalResultsDto

@Composable
fun PlaceItem(
    place: LocalResultsDto,
    onClick: () -> Unit
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

            // HEADER
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

            // QUÁN ĂN GẦN
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Quán ăn gần đây",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(5) {
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .height(180.dp),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    model = "https://picsum.photos/300/200",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Column(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Text(
                                        text = "Quán demo",
                                        style = MaterialTheme.typography.titleSmall
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Gần địa điểm này",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text("⭐ 4.5")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
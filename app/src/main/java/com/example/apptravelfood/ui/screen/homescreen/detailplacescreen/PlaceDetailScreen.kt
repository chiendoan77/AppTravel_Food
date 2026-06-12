package com.example.apptravelfood.ui.screen.homescreen.detailplacescreen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.ui.components.AppGreen
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppSmallTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    place: LocalResultsDto,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết địa điểm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val lat = place.gps_coordinates?.latitude
                            val lng = place.gps_coordinates?.longitude

                            if (lat != null && lng != null) {
                                val uri = "google.navigation:q=$lat,$lng".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = "Dẫn đường")
                    }
                }
            )
        }
    ) { padding ->

        AppPageSurface(modifier = Modifier.padding(padding), scrollable = true) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = place.thumbnail_large,
                    contentDescription = place.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = place.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppGreenStrong
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AppSmallTag(text = "${place.rating ?: 0.0} ★")
                        Text(text = place.rating?.toString() ?: "Chưa có đánh giá")
                    }

                    DetailInfoRow(
                        icon = Icons.Default.Category,
                        title = "Kiểu địa điểm",
                        value = place.type
                    )

                    DetailInfoRow(
                        icon = Icons.Default.LocationOn,
                        title = "Địa chỉ",
                        value = place.address ?: "Chưa có địa chỉ"
                    )

                    DetailInfoRow(
                        icon = Icons.Default.LocationOn,
                        title = "Tọa độ",
                        value = if (place.gps_coordinates != null) {
                            "${place.gps_coordinates.latitude}, ${place.gps_coordinates.longitude}"
                        } else {
                            "Chưa có tọa độ"
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Mô tả",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = place.description ?: "Chưa có mô tả",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(icon, null, tint = AppGreen)

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = AppGreenStrong
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
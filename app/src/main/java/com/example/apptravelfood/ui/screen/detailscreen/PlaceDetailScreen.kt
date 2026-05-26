package com.example.apptravelfood.ui.screen.detailscreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.apptravelfood.data.remote.dto.LocalResultsDto

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
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val lat = place.gps_coordinates?.latitude
                            val lng = place.gps_coordinates?.longitude

                            if (lat != null && lng != null) {
                                val uri = Uri.parse("google.navigation:q=$lat,$lng")
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

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = place.thumbnail,
                contentDescription = place.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = place.title,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(14.dp))

                DetailInfoRow(
                    icon = Icons.Default.Star,
                    title = "Đánh giá",
                    value = place.rating?.toString() ?: "Chưa có đánh giá"
                )

                DetailInfoRow(
                    icon = Icons.Default.Category,
                    title = "Kiểu địa điểm",
                    value = place.type ?: "Chưa có loại"
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

@Composable
fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            Icon(icon, null)

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
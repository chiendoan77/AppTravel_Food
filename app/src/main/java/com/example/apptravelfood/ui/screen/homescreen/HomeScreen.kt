package com.example.apptravelfood.ui.screen.homescreen

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.apptravelfood.core.untils.LocationHelper
import com.example.apptravelfood.core.untils.getAddressFromLocation
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.ui.components.AppGreenStrong
import com.example.apptravelfood.ui.components.AppPageSurface
import com.example.apptravelfood.ui.components.AppSearchBar
import com.example.apptravelfood.ui.components.PlaceItem

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onPlaceClick: (LocalResultsDto) -> Unit,
    onLocationFound: (String?, String?, String?) -> Unit,
    onFoodStoreClick: (FoodStoreEntity) -> Unit,
    onAddFoodStoreClick: (LocalResultsDto) -> Unit,
) {
    val context = LocalContext.current


    val getLocation = {
        LocationHelper(context).getCurrentLocation { lat, lng ->
            getAddressFromLocation(
                context,
                lat,
                lng
            ) { city, province, country ->

                Log.d(
                    "HomeScreen",
                    "City: $city, Province: $province, Country: $country"
                )

                onLocationFound(
                    city,
                    province,
                    country
                )
            }
        }
    }
    val permissionsToRequest =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->

        val locationGranted = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val notificationGranted = permissionsMap[Manifest.permission.POST_NOTIFICATIONS] == true

        if (locationGranted) {
            Log.d("!!!NOTI!!!", "Location Permission granted")
            getLocation()
        } else {
            Log.d("!!!NOTI!!!", "Location Permission denied")
        }

        if (notificationGranted) {
            Log.d("!!!NOTI!!!", "Notification permission granted")
        } else {
            Log.d("!!!NOTI!!!", "Notification permission denied")
        }
    }

    LaunchedEffect(Unit) {
        val allPermissionsGranted = permissionsToRequest.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            Log.d("!!!NOTI!!!", "All Permissions already granted")
            getLocation()
        } else {
            Log.d("!!!NOTI!!!", "Requesting permissions...")
            launcher.launch(permissionsToRequest)
        }
    }

    AppPageSurface {
        Text(
            text = "Khám phá mọi hành trình",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            color = AppGreenStrong
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Lỗi: ${uiState.error}")
                }
            }

            uiState.places.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Nhập địa điểm cần tìm")
                }
            }

            else -> {
                LazyColumn {
                    items(uiState.places) { place ->
                        val foodStores =
                            uiState.foodStoresByPlace[place.place_id ?: ""]
                                ?: emptyList()

                        PlaceItem(
                            place = place,
                            foodStores = foodStores,
                            onClick = {
                                onPlaceClick(place)
                            },
                            onAddFoodStoreClick = onAddFoodStoreClick,
                            onFoodStoreClick = onFoodStoreClick
                        )
                    }
                }
            }
        }
    }
}
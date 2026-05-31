package com.example.apptravelfood.ui.screen.homescreen

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.apptravelfood.core.untils.LocationHelper
import com.example.apptravelfood.core.untils.getAddressFromLocation
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.ui.components.AppSearchBar
import com.example.apptravelfood.ui.components.FloatingBottomBar
import com.example.apptravelfood.ui.components.PlaceItem
import com.example.apptravelfood.ui.navgation.AppRoute

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onPlaceClick: (LocalResultsDto) -> Unit,
    onBottomClick: (String) -> Unit,
    onLocationFound: (String?, String?, String?) -> Unit
) {
    val context = LocalContext.current
    val getLocation = {
        LocationHelper(context).getCurrentLocation { lat, lng ->
            getAddressFromLocation(
                context,
                lat,
                lng
            ) { city, province, country ->
                Log.d("HomeScreen", "City: $city, Province: $province, Country: $country")
                onLocationFound(
                    city,
                    province,
                    country
                )

            }
        }
    }
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                getLocation()
                Log.d("ABC", "Permission granted")
            } else {
                Log.d("ABC", "Permission denied")
            }
        }


    LaunchedEffect(Unit) {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.d("ABC", "Permission already granted")
            getLocation()
        } else {
            Log.d("ABC", "Requesting permission")
            launcher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    Scaffold(
        bottomBar = {
            FloatingBottomBar(
                selectedRoute = AppRoute.HOME,
                onItemClick = onBottomClick
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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

                else -> {
                    LazyColumn {
                        items(uiState.places) { place ->
                            PlaceItem(
                                place = place,
                                onClick = {
                                    onPlaceClick(place)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.apptravelfood.ui.navgation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.*
import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.ui.screen.detailscreen.PlaceDetailScreen
import com.example.apptravelfood.ui.screen.homescreen.HomeScreen
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNav(
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()
    var selectedPlace by remember { mutableStateOf<LocalResultsDto?>(null) }
    var currentLocation by rememberSaveable {
        mutableStateOf("")
    }
    var query by rememberSaveable {
        mutableStateOf("")
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.HOME
    ) {
        composable(AppRoute.HOME) {
            val uiState by homeViewModel.uiState.collectAsState()
            HomeScreen(
                uiState = uiState,

                query = query,

                onQueryChange = { newQuery ->
                    query = newQuery
                },

                onSearch = { searchQuery ->
                    homeViewModel.searchPlaces(
                        query = searchQuery,
                        location = currentLocation
                    )
                },

                onPlaceClick = { place ->
                    selectedPlace = place
                    navController.navigate(AppRoute.DETAIL)
                },

                onBottomClick = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onLocationFound = { city, province, country ->
                    currentLocation = AppConstant.getLocationFromProvince(province)
                }
            )
        }

        composable(AppRoute.DETAIL) {
            selectedPlace?.let { place ->
                PlaceDetailScreen(
                    place = place,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(AppRoute.CHECKIN) {
            androidx.compose.material3.Text("Màn hình check-in")
        }

        composable(AppRoute.SAVED) {
            androidx.compose.material3.Text("Màn hình đã lưu")
        }

        composable(AppRoute.PROFILE) {
            androidx.compose.material3.Text("Màn hình cá nhân")
        }
    }
}
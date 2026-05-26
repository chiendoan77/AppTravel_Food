package com.example.apptravelfood.ui.navgation


import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.ui.screen.detailscreen.PlaceDetailScreen
import com.example.apptravelfood.ui.screen.homescreen.HomeScreen
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModel

@Composable
fun AppNav(
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()
    var selectedPlace by remember { mutableStateOf<LocalResultsDto?>(null) }

    NavHost(
        navController = navController,
        startDestination = AppRoute.HOME
    ) {
        composable(AppRoute.HOME) {
            val uiState by homeViewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                homeViewModel.searchPlaces()
            }

            HomeScreen(
                uiState = uiState,
                onPlaceClick = { place ->
                    selectedPlace = place
                    navController.navigate(AppRoute.DETAIL)
                },
                onBottomClick = { route ->
                    navController.navigate(route)
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

        composable(AppRoute.MAP) {
            androidx.compose.material3.Text("Màn hình Map")
        }

        composable(AppRoute.SAVED) {
            androidx.compose.material3.Text("Màn hình đã lưu")
        }

        composable(AppRoute.PROFILE) {
            androidx.compose.material3.Text("Màn hình cá nhân")
        }
    }
}
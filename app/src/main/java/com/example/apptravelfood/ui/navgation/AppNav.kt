package com.example.apptravelfood.ui.navgation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.core.di.AppContainer
import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.ui.components.FloatingBottomBar
import com.example.apptravelfood.ui.screen.checkinscreen.CheckinRoute
import com.example.apptravelfood.ui.screen.checkinscreen.CheckinViewModel
import com.example.apptravelfood.ui.screen.checkinscreen.CheckinViewModelFactory
import com.example.apptravelfood.ui.screen.food.addfooditemscreen.AddFoodItemRoute
import com.example.apptravelfood.ui.screen.food.addfooditemscreen.AddFoodItemViewModel
import com.example.apptravelfood.ui.screen.food.addfooditemscreen.AddFoodItemViewModelFactory
import com.example.apptravelfood.ui.screen.food.addfoodstorescreen.AddFoodStoreRoute
import com.example.apptravelfood.ui.screen.food.addfoodstorescreen.AddFoodStoreViewModel
import com.example.apptravelfood.ui.screen.food.addfoodstorescreen.AddFoodStoreViewModelFactory
import com.example.apptravelfood.ui.screen.food.foodstoredetailscreen.FoodStoreDetailRoute
import com.example.apptravelfood.ui.screen.food.foodstoredetailscreen.FoodStoreDetailViewModel
import com.example.apptravelfood.ui.screen.food.foodstoredetailscreen.FoodStoreDetailViewModelFactory
import com.example.apptravelfood.ui.screen.homescreen.detailplacescreen.PlaceDetailScreen
import com.example.apptravelfood.ui.screen.historycreen.HistoryRoute
import com.example.apptravelfood.ui.screen.historycreen.HistoryViewModel
import com.example.apptravelfood.ui.screen.historycreen.HistoryViewModelFactory
import com.example.apptravelfood.ui.screen.homescreen.HomeScreen
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModel
import com.example.apptravelfood.ui.screen.profilescreen.ProfileRoute
import com.example.apptravelfood.ui.screen.profilescreen.ProfileViewModel
import com.example.apptravelfood.ui.screen.profilescreen.ProfileViewModelFactory

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNav(
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var selectedPlace by remember {
        mutableStateOf<LocalResultsDto?>(null)
    }

    var currentLocation by rememberSaveable {
        mutableStateOf("")
    }

    var query by rememberSaveable {
        mutableStateOf("")
    }

    var selectedFoodStore by remember {
        mutableStateOf<FoodStoreEntity?>(null)
    }

    var selectedAddPlace by remember {
        mutableStateOf<LocalResultsDto?>(null)
    }

    var selectedFoodStoreForAddItem by remember {
        mutableStateOf<FoodStoreEntity?>(null)
    }

    var selectedFoodItem by remember {
        mutableStateOf<FoodItemEntity?>(null)
    }
    Scaffold(
        bottomBar = {
            FloatingBottomBar(
                selectedRoute = currentRoute ?: AppRoute.HOME,
                onItemClick = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true

                        popUpTo(AppRoute.HOME) {
                            saveState = true
                        }

                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = AppRoute.HOME,
            modifier = Modifier.padding(padding)
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

                    onLocationFound = { city, province, country ->
                        currentLocation =
                            AppConstant.getLocationFromProvince(province)
                    },
                    onFoodStoreClick = { store ->
                        selectedFoodStore = store
                        navController.navigate(AppRoute.FOOD_STORE_DETAIL)
                    },
                    onAddFoodStoreClick = { place ->
                        selectedAddPlace = place
                        navController.navigate(AppRoute.ADD_FOOD_STORE)
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
                val checkinViewModel: CheckinViewModel = viewModel(
                    factory = CheckinViewModelFactory(
                        checkinRepository = AppContainer.checkinRepository,
                        pointHistoryRepository = AppContainer.pointHistoryRepository,
                        userRepository = AppContainer.userRepository
                    )
                )

                CheckinRoute(
                    viewModel = checkinViewModel,
                    userId = 1L
                )
            }

            composable(AppRoute.HISTORY) {
                val historyViewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModelFactory(
                        checkinRepository = AppContainer.checkinRepository,
                        pointHistoryRepository = AppContainer.pointHistoryRepository
                    )
                )

                HistoryRoute(
                    viewModel = historyViewModel,
                    userId = 1L
                )
            }

            composable(AppRoute.PROFILE) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory(
                        userRepository = AppContainer.userRepository
                    )
                )

                ProfileRoute(
                    viewModel = profileViewModel,
                    userId = 1L
                )
            }
            composable(AppRoute.FOOD_STORE_DETAIL) {
                val foodStoreDetailViewModel: FoodStoreDetailViewModel = viewModel(
                    factory = FoodStoreDetailViewModelFactory(
                        foodItemRepository = AppContainer.foodItemRepository,
                        reviewRepository = AppContainer.foodStoreReviewRepository
                    )
                )

                selectedFoodStore?.let { store ->
                    FoodStoreDetailRoute(
                        viewModel = foodStoreDetailViewModel,
                        store = store,
                        userId = 1L,
                        onBack = {
                            navController.popBackStack()
                        },
                        onAddFoodItemClick = { foodStore ->
                            selectedFoodStoreForAddItem = foodStore
                            navController.navigate(AppRoute.ADD_FOOD_ITEM)
                        },
                        onFoodItemClick = {food ->
                            selectedFoodItem = food
                            navController.navigate(
                                AppRoute.ADD_FOOD_ITEM
                            )
                        }
                    )
                }
            }
            composable(AppRoute.ADD_FOOD_STORE) {
                val addFoodStoreViewModel: AddFoodStoreViewModel = viewModel(
                    factory = AddFoodStoreViewModelFactory(
                        foodStoreRepository = AppContainer.foodStoreRepository
                    )
                )

                selectedAddPlace?.let { place ->
                    AddFoodStoreRoute(
                        viewModel = addFoodStoreViewModel,
                        place = place,
                        userId = 1L,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSuccess = {
                            navController.popBackStack()
                            homeViewModel.searchPlaces(
                                query = query,
                                location = currentLocation
                            )
                        }
                    )
                }
            }
            composable(AppRoute.ADD_FOOD_ITEM) {
                val addFoodItemViewModel: AddFoodItemViewModel = viewModel(
                    factory = AddFoodItemViewModelFactory(
                        foodItemRepository = AppContainer.foodItemRepository
                    )
                )

                selectedFoodStoreForAddItem?.let { store ->
                    AddFoodItemRoute(
                        viewModel = addFoodItemViewModel,
                        store = store,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
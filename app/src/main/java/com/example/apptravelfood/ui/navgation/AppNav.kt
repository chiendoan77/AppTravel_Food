package com.example.apptravelfood.ui.navgation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.apptravelfood.ui.screen.authscreen.AuthRoute
import com.example.apptravelfood.ui.screen.authscreen.AuthViewModel
import com.example.apptravelfood.ui.screen.authscreen.AuthViewModelFactory
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
import com.example.apptravelfood.ui.screen.historyscreen.HistoryRoute
import com.example.apptravelfood.ui.screen.historyscreen.HistoryViewModel
import com.example.apptravelfood.ui.screen.historyscreen.HistoryViewModelFactory
import com.example.apptravelfood.ui.screen.homescreen.HomeScreen
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModel
import com.example.apptravelfood.ui.screen.profilescreen.ProfileRoute
import com.example.apptravelfood.ui.screen.profilescreen.ProfileViewModel
import com.example.apptravelfood.ui.screen.profilescreen.ProfileViewModelFactory
import com.example.apptravelfood.ui.screen.profilescreen.setting.ProfileSettingScreen
import com.example.apptravelfood.ui.screen.profilescreen.term.TermsScreen

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

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    var loggedUserId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    val showBottomBar = currentRoute != AppRoute.AUTH

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = AppRoute.AUTH,
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
                        userRepository = AppContainer.userRepository,
                        firebaseRepository = AppContainer.firebaseRepository
                    )
                )

                CheckinRoute(
                    viewModel = checkinViewModel,
                    userId = loggedUserId ?: return@composable
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
                    userId = loggedUserId ?: return@composable
                )
            }

            composable(AppRoute.PROFILE) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory(
                        userRepository = AppContainer.userRepository,
                        firebaseRepository = AppContainer.firebaseRepository
                    )
                )

                ProfileRoute(
                    viewModel = profileViewModel,
                    userId = loggedUserId ?: return@composable,
                    onSettingClick = {
                        navController.navigate(AppRoute.PROFILE_SETTING)
                    },
                    onTermsClick = {
                        navController.navigate(AppRoute.TERMS)
                    },
                    onLogoutClick = {
                        showLogoutDialog = true
                    }
                )
            }
            composable(AppRoute.FOOD_STORE_DETAIL) {
                val foodStoreDetailViewModel: FoodStoreDetailViewModel = viewModel(
                    factory = FoodStoreDetailViewModelFactory(
                        foodItemRepository = AppContainer.foodItemRepository,
                        reviewRepository = AppContainer.foodStoreReviewRepository,
                        firebaseRepository = AppContainer.firebaseRepository
                    )
                )

                selectedFoodStore?.let { store ->
                    FoodStoreDetailRoute(
                        viewModel = foodStoreDetailViewModel,
                        store = store,
                        userId = loggedUserId ?: return@composable,
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
                        foodStoreRepository = AppContainer.foodStoreRepository,
                        firebaseRepository = AppContainer.firebaseRepository
                    )
                )

                selectedAddPlace?.let { place ->
                    AddFoodStoreRoute(
                        viewModel = addFoodStoreViewModel,
                        place = place,
                        userId = loggedUserId ?: return@composable,
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
                        foodItemRepository = AppContainer.foodItemRepository,
                        firebaseRepository = AppContainer.firebaseRepository
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
            composable(AppRoute.AUTH) {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(
                        userRepository = AppContainer.userRepository,
                        syncRepository = AppContainer.syncRepository,
                        firebaseRepository = AppContainer.firebaseRepository
                    )
                )

                AuthRoute(
                    viewModel = authViewModel,
                    onLoginSuccess = { userId ->
                        loggedUserId = userId

                        navController.navigate(AppRoute.HOME) {
                            popUpTo(AppRoute.AUTH) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(AppRoute.PROFILE_SETTING) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory(
                        userRepository = AppContainer.userRepository,
                        firebaseRepository = AppContainer.firebaseRepository
                    )
                )

                val uiState by profileViewModel.uiState.collectAsState()
                val userId = loggedUserId ?: return@composable

                LaunchedEffect(userId) {
                    profileViewModel.loadUser(userId)
                }

                ProfileSettingScreen(
                    uiState = uiState,
                    onBack = {
                        navController.popBackStack()
                    },
                    onUpdateName = { name ->
                        profileViewModel.updateName(userId, name)
                    },
                    onUpdatePhone = { phone ->
                        profileViewModel.updatePhone(userId, phone)
                    },
                    onUpdatePassword = { password ->
                        profileViewModel.updatePassword(userId, password)
                    },
                    onUpdateBiometric = { userId, enabled ->
                        profileViewModel.updateBiometricEnabled(
                            userId = userId,
                            enabled = enabled
                        )
                    }
                )
            }
            composable(AppRoute.TERMS) {
                TermsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = {
                    showLogoutDialog = false
                },
                title = {
                    Text("Đăng xuất")
                },
                text = {
                    Text("Bạn có chắc muốn đăng xuất không?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            loggedUserId = null

                            navController.navigate(AppRoute.AUTH) {
                                popUpTo(0)
                            }
                        }
                    ) {
                        Text("Đăng xuất")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                        }
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}
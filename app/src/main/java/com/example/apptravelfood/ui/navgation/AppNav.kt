package com.example.apptravelfood.ui.navgation

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.apptravelfood.core.constant.AppConstant
import com.example.apptravelfood.core.di.AppContainer
import com.example.apptravelfood.core.notification.NotificationForegroundService
import com.example.apptravelfood.core.session.SessionManager
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
import com.example.apptravelfood.ui.screen.historyscreen.HistoryRoute
import com.example.apptravelfood.ui.screen.historyscreen.HistoryViewModel
import com.example.apptravelfood.ui.screen.historyscreen.HistoryViewModelFactory
import com.example.apptravelfood.ui.screen.homescreen.HomeScreen
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModel
import com.example.apptravelfood.ui.screen.homescreen.detailplacescreen.PlaceDetailScreen
import com.example.apptravelfood.ui.screen.profilescreen.ProfileRoute
import com.example.apptravelfood.ui.screen.profilescreen.ProfileViewModel
import com.example.apptravelfood.ui.screen.profilescreen.ProfileViewModelFactory
import com.example.apptravelfood.ui.screen.profilescreen.setting.ProfileSettingScreen
import com.example.apptravelfood.ui.screen.profilescreen.term.TermsScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNav(
    homeViewModel: HomeViewModel,
    initialFoodStoreId: Long? = null,
    initialReviewId: Long? = null
) {
    val context = LocalContext.current
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

    val sessionManager = remember {
        SessionManager(context)
    }

    var loggedUserId by rememberSaveable {
        mutableStateOf<Long?>(sessionManager.getLoggedUserId())
    }

    // Xử lý deep link từ thông báo khi app đang chạy
    LaunchedEffect(initialFoodStoreId, initialReviewId) {
        if (initialFoodStoreId != null && loggedUserId != null) {
            val store = AppContainer.foodStoreRepository.getFoodStoreById(initialFoodStoreId)
            if (store != null) {
                selectedFoodStore = store
                navController.navigate(AppRoute.FOOD_STORE_DETAIL)
            }
        }
    }

    LaunchedEffect(loggedUserId) {
        loggedUserId?.let { userId ->
            Log.d("AppNav", "Starting NotificationForegroundService for userId=$userId")
            val intent = Intent(
                context,
                NotificationForegroundService::class.java
            ).putExtra("userId", userId)

            ContextCompat.startForegroundService(
                context,
                intent
            )
        }
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
            startDestination = if (loggedUserId != null) AppRoute.HOME else AppRoute.AUTH,
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
                        pointHistoryRepository = AppContainer.pointHistoryRepository,
                        syncRepository = AppContainer.syncRepository,
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
                        firebaseRepository = AppContainer.firebaseRepository,
                        otpRepository = AppContainer.otpRepository,
                        supabaseStorageRepository = AppContainer.supabaseStorageRepository
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
                        showLogoutDialog = false
                        loggedUserId = null
                        sessionManager.logout()

                        context.stopService(
                            Intent(context, NotificationForegroundService::class.java)
                        )

                        navController.navigate(AppRoute.AUTH) {
                            popUpTo(0)
                        }
                    }
                )
            }
            composable(AppRoute.FOOD_STORE_DETAIL) {
                val foodStoreDetailViewModel: FoodStoreDetailViewModel = viewModel(
                    factory = FoodStoreDetailViewModelFactory(
                        foodItemRepository = AppContainer.foodItemRepository,
                        foodStoreRepository = AppContainer.foodStoreRepository,
                        reviewRepository = AppContainer.foodStoreReviewRepository,
                        firebaseRepository = AppContainer.firebaseRepository,
                        userRepository = AppContainer.userRepository
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
                            selectedFoodItem = null
                            selectedFoodStoreForAddItem = foodStore
                            navController.navigate(AppRoute.ADD_FOOD_ITEM)
                        },
                        onFoodItemClick = { food ->
                            selectedFoodItem = food
                            selectedFoodStoreForAddItem = store
                            navController.navigate(AppRoute.ADD_FOOD_ITEM)
                        }
                    )
                }
            }
            composable(AppRoute.ADD_FOOD_STORE) {
                val addFoodStoreViewModel: AddFoodStoreViewModel = viewModel(
                    factory = AddFoodStoreViewModelFactory(
                        foodStoreRepository = AppContainer.foodStoreRepository,
                        firebaseRepository = AppContainer.firebaseRepository,
                        addressRepository = AppContainer.addressRepository,
                        supabaseStorageRepository = AppContainer.supabaseStorageRepository
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
                            selectedFoodItem = null
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
                        firebaseRepository = AppContainer.firebaseRepository,
                        supabaseStorageRepository = AppContainer.supabaseStorageRepository
                    )
                )

                selectedFoodStoreForAddItem?.let { store ->
                    AddFoodItemRoute(
                        viewModel = addFoodItemViewModel,
                        store = store,
                        editFoodItem = selectedFoodItem,
                        onBack = {
                            selectedFoodItem = null
                            navController.popBackStack()
                        },
                        onSuccess = {
                            selectedFoodItem = null
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
                        firebaseRepository = AppContainer.firebaseRepository,
                        otpRepository = AppContainer.otpRepository,
                        firebaseAuthRepository = AppContainer.firebaseAuthRepository
                    )
                )

                AuthRoute(
                    viewModel = authViewModel,
                    onLoginSuccess = { userId ->
                        loggedUserId = userId
                        sessionManager.saveLogin(userId)

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
                        firebaseRepository = AppContainer.firebaseRepository,
                        otpRepository = AppContainer.otpRepository,
                        supabaseStorageRepository = AppContainer.supabaseStorageRepository
                    )
                )

                val context = LocalContext.current
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
                    onSaveProfile = { name, phone, avatarUri ->
                        profileViewModel.updateProfile(
                            context = context,
                            userId = userId,
                            name = name,
                            phone = phone,
                            avatarUri = avatarUri
                        )
                    },
                    onUpdateBiometric = { userId, enabled ->
                        profileViewModel.updateBiometricEnabled(
                            userId = userId,
                            enabled = enabled
                        )
                    },
                    onSendPasswordOtp = {
                        profileViewModel.sendPasswordOtp(userId)
                    },
                    onUpdatePasswordWithOtp = { otp, newPassword ->
                        profileViewModel.updatePasswordWithOtp(
                            userId = userId,
                            otp = otp,
                            newPassword = newPassword
                        )
                    },
                    onClearStatus = {
                        profileViewModel.clearStatus()
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
                            sessionManager.logout()

                            context.stopService(
                                Intent(context, NotificationForegroundService::class.java)
                            )

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
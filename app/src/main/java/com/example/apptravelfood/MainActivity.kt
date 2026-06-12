package com.example.apptravelfood

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptravelfood.core.di.AppContainer
import com.example.apptravelfood.core.network.RetrofitClient
import com.example.apptravelfood.data.local.database.DatabaseProvider
import com.example.apptravelfood.data.repository.PlaceRepository
import com.example.apptravelfood.ui.navgation.AppNav
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModel
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModelFactory

class MainActivity : FragmentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("!!!NOTI!!!", "Notification permission granted")
        } else {
            Log.e("!!!NOTI!!!", "Notification permission denied")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("!!!NOTI!!!", "MainActivity onCreate - Checking permissions")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("!!!NOTI!!!", "Requesting POST_NOTIFICATIONS permission")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d("!!!NOTI!!!", "POST_NOTIFICATIONS permission already granted")
            }
        }

        val database = DatabaseProvider.getDatabase(this)
        AppContainer.init(database)

        val foodStoreIdFromIntent = intent.getLongExtra("foodStoreId", -1L)
        val reviewIdFromIntent = intent.getLongExtra("reviewId", -1L)

        setContent {
            val api = RetrofitClient.serpApi

            val repository = PlaceRepository(api)

            val factory = HomeViewModelFactory(
                repository = repository,
                placeRepositoryLocal = AppContainer.placeRepository,
                foodStoreRepository = AppContainer.foodStoreRepository,
                firebaseRepository = AppContainer.firebaseRepository
            )

            val homeViewModel: HomeViewModel = viewModel(
                factory = factory
            )

            AppNav(
                homeViewModel = homeViewModel,
                initialFoodStoreId = if (foodStoreIdFromIntent != -1L) foodStoreIdFromIntent else null,
                initialReviewId = if (reviewIdFromIntent != -1L) reviewIdFromIntent else null
            )
        }
    }
}
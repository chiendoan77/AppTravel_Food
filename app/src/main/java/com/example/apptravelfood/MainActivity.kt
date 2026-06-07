package com.example.apptravelfood

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = DatabaseProvider.getDatabase(this)
        AppContainer.init(database)

        setContent {
            val api = RetrofitClient.api

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
                homeViewModel = homeViewModel
            )
        }
    }
}
package com.example.apptravelfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptravelfood.core.network.RetrofitClient
import com.example.apptravelfood.data.reponsitory.PlaceRepository
import com.example.apptravelfood.ui.navgation.AppNav
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModel
import com.example.apptravelfood.ui.screen.homescreen.HomeViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val api = RetrofitClient.api
            val repository = PlaceRepository(api)
            val factory = HomeViewModelFactory(repository)

            val homeViewModel: HomeViewModel = viewModel(
                factory = factory
            )

            AppNav(homeViewModel = homeViewModel)
        }
    }
}
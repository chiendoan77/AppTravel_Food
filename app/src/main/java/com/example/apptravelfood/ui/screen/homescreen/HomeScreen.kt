package com.example.apptravelfood.ui.screen.homescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.apptravelfood.data.remote.dto.LocalResultsDto
import com.example.apptravelfood.ui.components.FloatingBottomBar
import com.example.apptravelfood.ui.components.PlaceItem
import com.example.apptravelfood.ui.navgation.AppRoute
import com.example.apptravelfood.ui.screen.detailscreen.PlaceDetailScreen

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPlaceClick: (LocalResultsDto) -> Unit,
    onBottomClick: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            FloatingBottomBar(
                selectedRoute = AppRoute.HOME,
                onItemClick = onBottomClick
            )
        }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Text(text = "Lỗi: ${uiState.error}")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
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
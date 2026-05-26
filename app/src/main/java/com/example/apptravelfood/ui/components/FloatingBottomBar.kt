package com.example.apptravelfood.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.apptravelfood.ui.navgation.AppRoute


@Composable
fun FloatingBottomBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedRoute == AppRoute.HOME,
            onClick = { onItemClick(AppRoute.HOME) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = selectedRoute == AppRoute.MAP,
            onClick = { onItemClick(AppRoute.MAP) },
            icon = { Icon(Icons.Default.Map, null) },
            label = { Text("Map") }
        )

        NavigationBarItem(
            selected = selectedRoute == AppRoute.SAVED,
            onClick = { onItemClick(AppRoute.SAVED) },
            icon = { Icon(Icons.Default.Favorite, null) },
            label = { Text("Lưu") }
        )

        NavigationBarItem(
            selected = selectedRoute == AppRoute.PROFILE,
            onClick = { onItemClick(AppRoute.PROFILE) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Tôi") }
        )
    }
}
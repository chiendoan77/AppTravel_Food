package com.example.apptravelfood.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.example.apptravelfood.ui.navgation.AppRoute


@Composable
fun FloatingBottomBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(68.dp),
            colors = NavigationBarDefaults.colors(
                indicatorColor = Color(0xFFE8F6EA),
                containerColor = Color.White,
                selectedIconColor = Color(0xFF2E8C4A),
                unselectedIconColor = Color(0xFF8E9BA5),
                selectedTextColor = Color(0xFF2E8C4A),
                unselectedTextColor = Color(0xFF8E9BA5)
            )
        ) {
            NavigationBarItem(
            selected = selectedRoute == AppRoute.HOME,
            onClick = { onItemClick(AppRoute.HOME) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Trang chủ") }
        )

            NavigationBarItem(
            selected = selectedRoute == AppRoute.CHECKIN,
            onClick = { onItemClick(AppRoute.CHECKIN) },
            icon = { Icon(Icons.Default.AppRegistration, null) },
            label = { Text("Checkin") }
        )

            NavigationBarItem(
            selected = selectedRoute == AppRoute.HISTORY,
            onClick = { onItemClick(AppRoute.HISTORY) },
            icon = { Icon(Icons.Default.History, null) },
            label = { Text("Lịch sử") }
        )

            NavigationBarItem(
            selected = selectedRoute == AppRoute.PROFILE,
            onClick = { onItemClick(AppRoute.PROFILE) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Tôi") }
        )
        }
    }
}
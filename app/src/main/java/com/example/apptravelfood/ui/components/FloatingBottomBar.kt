package com.example.apptravelfood.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.apptravelfood.ui.navgation.AppRoute


@Composable
fun FloatingBottomBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit,
) {
    val navigationItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = Color(0xFFE8F6EA),
        selectedIconColor = Color(0xFF2E8C4A),
        unselectedIconColor = Color(0xFF8E9BA5),
        selectedTextColor = Color(0xFF2E8C4A),
        unselectedTextColor = Color(0xFF8E9BA5)
    )

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
            containerColor = Color.White,
        ) {
            NavigationBarItem(
                selected = selectedRoute == AppRoute.HOME,
                onClick = { onItemClick(AppRoute.HOME) },
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Trang chủ") },
                colors = navigationItemColors
            )

            NavigationBarItem(
                selected = selectedRoute == AppRoute.CHECKIN,
                onClick = { onItemClick(AppRoute.CHECKIN) },
                icon = { Icon(Icons.Default.AppRegistration, null) },
                label = { Text("Checkin") },
                colors = navigationItemColors
            )

            NavigationBarItem(
                selected = selectedRoute == AppRoute.HISTORY,
                onClick = { onItemClick(AppRoute.HISTORY) },
                icon = { Icon(Icons.Default.History, null) },
                label = { Text("Lịch sử") },
                colors = navigationItemColors
            )

            NavigationBarItem(
                selected = selectedRoute == AppRoute.PROFILE,
                onClick = { onItemClick(AppRoute.PROFILE) },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Tôi") },
                colors = navigationItemColors
            )
        }
    }
}

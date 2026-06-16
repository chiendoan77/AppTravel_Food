package com.example.apptravelfood.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apptravelfood.ui.navgation.AppRoute

private data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

private val navItems = listOf(
    NavItem(AppRoute.HOME, Icons.Default.Home, "Trang chủ"),
    NavItem(AppRoute.CHECKIN, Icons.Default.AppRegistration, "Checkin"),
    NavItem(AppRoute.HISTORY, Icons.Default.History, "Lịch sử"),
    NavItem(AppRoute.PROFILE, Icons.Default.Person, "Tôi"),
)

@Composable
fun FloatingBottomBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 20.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                BottomNavItem(
                    item = item,
                    selected = selectedRoute == item.route,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconTint by animateColorAsState(
        targetValue = if (selected) AppGreen else Color(0xFFAAAAAA),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconTint"
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) AppGreen else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "labelColor"
    )
    val pillWidth by animateDpAsState(
        targetValue = if (selected) 72.dp else 48.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pillWidth"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp)
    ) {
        // Pill indicator + icon
        Box(
            modifier = Modifier
                .width(pillWidth)
                .height(36.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (selected) AppGreen.copy(alpha = 0.12f) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor,
            maxLines = 1
        )
    }
}

package com.moneynest.app.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.moneynest.app.navigation.Screen

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val primaryPurple = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
    val selectedBackground = MaterialTheme.colorScheme.primaryContainer

    val navigationItems = listOf(
        Triple(Screen.Dashboard.route, "Home", Icons.Default.Home),
        Triple(Screen.History.route, "History", Icons.Default.History),
        Triple(Screen.Scanner.route, "Scanner", Icons.Default.CameraAlt),
        Triple(Screen.Analytics.route, "Analytics", Icons.Default.BarChart)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 4.dp
    ) {
        navigationItems.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        onNavigate(route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(label)
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryPurple,
                    selectedTextColor = primaryPurple,
                    indicatorColor = selectedBackground,
                    unselectedIconColor = inactiveColor,
                    unselectedTextColor = inactiveColor
                )
            )
        }
    }
}

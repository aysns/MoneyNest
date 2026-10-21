
package com.moneynest.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.moneynest.app.feature.dashboard.DashboardScreen
import com.moneynest.app.feature.history.HistoryScreen
import com.moneynest.app.feature.scanner.ScannerScreen
import com.moneynest.app.feature.transaction.AddTransactionScreen
import com.moneynest.app.feature.analytics.AnalyticsScreen
import com.moneynest.app.navigation.components.BottomNavBar

@Composable
fun MoneyNestNavHost() {

    val navController = rememberNavController()

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // DASHBOARD
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onAddTransactionClick = {
                        navController.navigate(
                            Screen.AddTransaction.route
                        )
                    }
                )
            }

            // HISTORY
            composable(Screen.History.route) {
                HistoryScreen()
            }

            // SCANNER
            composable(Screen.Scanner.route) {
                ScannerScreen()
            }

            // ANALYTICS - NEW
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }

            // ADD TRANSACTION
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen()
            }
        }
    }
}

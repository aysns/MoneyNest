package com.moneynest.app.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object History : Screen("history")
    data object Scanner : Screen("scanner")
    data object Analytics : Screen("analytics")
    data object AddTransaction : Screen("add_transaction")
}

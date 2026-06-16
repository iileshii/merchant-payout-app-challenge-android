package com.example.androidinterview.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Transactions :
        Screen("transactions", "Transactions", Icons.AutoMirrored.Filled.List)

    data object Payouts : Screen("payouts", "Payouts", Icons.Default.ShoppingCart)
}

package com.example.chatapp.navigation

sealed class Screen(val route: String) {
    object SignIn: Screen("SigninScreen")
    object Home: Screen("Home")
    object NumberScreen: Screen("NumberScreen")
    object QrScannerScreen: Screen("qrScannerScreen")


}
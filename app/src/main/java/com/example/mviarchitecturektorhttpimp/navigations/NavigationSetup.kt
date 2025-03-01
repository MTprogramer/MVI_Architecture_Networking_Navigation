package com.example.mviarchitecturektorhttpimp.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mviarchitecturektorhttpimp.Screens.PostScreen
import com.example.mviarchitecturektorhttpimp.Screens.PostScreenMethod2

@Composable
fun NavigationSetup() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "postScreen") {
        composable("postScreen") {
            PostScreen(navController = navController)
        }
        composable("nextScreen") {
            PostScreenMethod2()
        }
    }
}
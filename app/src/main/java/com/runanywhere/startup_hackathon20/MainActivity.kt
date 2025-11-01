package com.runanywhere.startup_hackathon20

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.runanywhere.startup_hackathon20.screens.*
import com.runanywhere.startup_hackathon20.ui.theme.Startup_hackathon20Theme
import com.runanywhere.startup_hackathon20.viewmodels.AuthViewModel
import com.runanywhere.startup_hackathon20.viewmodels.ScanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Startup_hackathon20Theme {
                BiteCheckApp()
            }
        }
    }
}

@Composable
fun BiteCheckApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val scanViewModel: ScanViewModel = viewModel()

    val currentUserId by authViewModel.currentUserId.collectAsState()
    val scanResult by scanViewModel.scanResult.collectAsState()
    val isLoading by scanViewModel.isLoading.collectAsState()

    // Determine start destination based on auth state
    val startDestination = if (authViewModel.isUserLoggedIn()) "home" else "login"

    // Handle navigation when scan completes
    LaunchedEffect(scanResult) {
        if (scanResult != null && !isLoading) {
            navController.navigate("results") {
                popUpTo("loading") { inclusive = true }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login Screen
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    authViewModel = authViewModel
                )
            }

            // Home Screen
            composable("home") {
                HomeScreen(
                    userId = currentUserId,
                    onScanClick = {
                        navController.navigate("camera")
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // Camera Screen
            composable("camera") {
                CameraScreen(
                    onImageCaptured = { bitmap ->
                        // Navigate to loading screen
                        navController.navigate("loading")
                        // Start processing in background with userId
                        scanViewModel.processFoodLabel(bitmap, currentUserId)
                    }
                )
            }

            // Loading Screen
            composable("loading") {
                if (isLoading) {
                    LoadingScreen()
                }
            }

            // Results Screen
            composable("results") {
                scanResult?.let { result ->
                    ResultsScreen(
                        scanResult = result,
                        onScanAnother = {
                            scanViewModel.clearResult()
                            navController.navigate("camera") {
                                popUpTo("home") { inclusive = false }
                            }
                        },
                        onReturnHome = {
                            scanViewModel.clearResult()
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
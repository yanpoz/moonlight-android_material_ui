package com.limelight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.limelight.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.limelight.ui.theme.MoonlightandroidTheme


class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MoonlightandroidTheme {
                mainViewModel = viewModel()
                val navController = rememberNavController()

                mainViewModel.bindComputerManagerService(this@MainActivity)

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onSettingsClick = { navController.navigate("settings") },
                            viewModel = mainViewModel
                        )
                    }
                    composable("settings") {
                        SettingsScreen()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::mainViewModel.isInitialized) { // Ensure ViewModel is initialized
            mainViewModel.onUiResumed()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mainViewModel.isInitialized) { // Ensure ViewModel is initialized
            mainViewModel.onUiPaused()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mainViewModel.isInitialized) { // Ensure ViewModel is initialized
            mainViewModel.unbindComputerManagerService(this)
        }
    }
}

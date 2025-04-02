package com.limelight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
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

        setContent {
            MoonlightandroidTheme {
                mainViewModel = viewModel()
                val navController = rememberNavController()

                mainViewModel.bindService(this@MainActivity)

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

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.unbindService(this)
    }
}

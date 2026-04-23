package com.limelight

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInputModeManager
import com.limelight.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.limelight.preferences.PreferenceConfiguration
import com.limelight.ui.MainScreen
import com.limelight.ui.SettingsScreen
import com.limelight.ui.theme.MoonlightAndroidTheme
import com.limelight.ui.utils.InputMethod
import com.limelight.ui.utils.LocalInputMethod
import com.limelight.ui.utils.toInputMode
import com.limelight.viewmodel.SettingsViewModel


class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var prefs: SharedPreferences
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { p, key ->
        if (key == PreferenceConfiguration.THEME_PREF_STRING) {
            themeState = p.getString(key, PreferenceConfiguration.DEFAULT_THEME) ?:
                PreferenceConfiguration.DEFAULT_THEME
        } else if (key == PreferenceConfiguration.DYNAMIC_THEME_PREF_STRING) {
            dynamicThemeState = p.getBoolean(key, PreferenceConfiguration.DEFAULT_DYNAMIC_THEME)
        }
    }
    private var themeState by mutableStateOf(PreferenceConfiguration.DEFAULT_THEME)
    private var dynamicThemeState by mutableStateOf(PreferenceConfiguration.DEFAULT_DYNAMIC_THEME)
    private var inputMethodState by mutableStateOf(InputMethod.Touch)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        themeState = prefs.getString(
            PreferenceConfiguration.THEME_PREF_STRING,
            PreferenceConfiguration.DEFAULT_THEME
        ) ?:
            PreferenceConfiguration.DEFAULT_THEME
        dynamicThemeState = prefs.getBoolean(
            PreferenceConfiguration.DYNAMIC_THEME_PREF_STRING,
            PreferenceConfiguration.DEFAULT_DYNAMIC_THEME
        )
        prefs.registerOnSharedPreferenceChangeListener(listener)

        setContent {
            MoonlightAndroidTheme(
                theme = themeState,
                dynamicColor = dynamicThemeState
            ) {
                val inputModeManager = LocalInputModeManager.current
                LaunchedEffect(inputMethodState) {
                    inputModeManager.requestInputMode(inputMethodState.toInputMode())
                }

                CompositionLocalProvider(LocalInputMethod provides inputMethodState) {
                    mainViewModel = viewModel()
                    settingsViewModel = viewModel()

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
                            SettingsScreen(
                                viewModel = settingsViewModel
                            )
                        }
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
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
        if (::mainViewModel.isInitialized) { // Ensure ViewModel is initialized
            mainViewModel.unbindComputerManagerService(this)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.source and InputDevice.SOURCE_KEYBOARD == InputDevice.SOURCE_KEYBOARD ||
            event.source and InputDevice.SOURCE_DPAD == InputDevice.SOURCE_DPAD ||
            event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            inputMethodState = InputMethod.Dpad
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_MOUSE == InputDevice.SOURCE_MOUSE) {
            inputMethodState = InputMethod.Mouse
        } else if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            inputMethodState = InputMethod.Dpad
        }
        return super.dispatchGenericMotionEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_TOUCHSCREEN == InputDevice.SOURCE_TOUCHSCREEN) {
            inputMethodState = InputMethod.Touch
        } else if (event.source and InputDevice.SOURCE_MOUSE == InputDevice.SOURCE_MOUSE) {
            inputMethodState = InputMethod.Mouse
        }
        return super.dispatchTouchEvent(event)
    }
}

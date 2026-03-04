package com.limelight.viewmodel.components

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.limelight.preferences.PreferenceConfiguration

data class QuickSettingsUiState(
    val showDialog: Boolean = false,
    val fps: String = PreferenceConfiguration.DEFAULT_FPS
)

class QuickSettingsHandler {
    var uiState by mutableStateOf(QuickSettingsUiState())
        private set

    fun onShowQuickSettings(currentFps: String) {
        uiState = QuickSettingsUiState(showDialog = true, fps = currentFps)
    }

    fun onFpsChanged(context: Context, fps: String) {
        uiState = uiState.copy(fps = fps)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(PreferenceConfiguration.FPS_PREF_STRING, fps)
        }
    }

    fun onDismissQuickSettings() {
        uiState = QuickSettingsUiState()
    }
}

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
    val fps: String = PreferenceConfiguration.DEFAULT_FPS,
    val resolution: String = PreferenceConfiguration.DEFAULT_RESOLUTION,
    val bitrate: Float = 0f
)

class QuickSettingsHandler {
    var uiState by mutableStateOf(QuickSettingsUiState())
        private set

    fun onShowQuickSettings(currentFps: String, currentResolution: String, currentBitrate: Float) {
        uiState = QuickSettingsUiState(
            showDialog = true,
            fps = currentFps,
            resolution = currentResolution,
            bitrate = currentBitrate
        )
    }

    fun onFpsChanged(context: Context, fps: String) {
        uiState = uiState.copy(fps = fps)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(PreferenceConfiguration.FPS_PREF_STRING, fps)
        }
    }

    fun onResolutionChanged(context: Context, resolution: String) {
        uiState = uiState.copy(resolution = resolution)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(PreferenceConfiguration.RESOLUTION_PREF_STRING, resolution)
        }
    }

    fun onBitrateChanged(context: Context, bitrate: Float) {
        uiState = uiState.copy(bitrate = bitrate)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putInt(PreferenceConfiguration.BITRATE_PREF_STRING, (bitrate * 1000).toInt())
        }
    }

    fun onDismissQuickSettings() {
        uiState = QuickSettingsUiState()
    }
}

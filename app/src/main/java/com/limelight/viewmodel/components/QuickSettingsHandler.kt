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
    val bitrate: Float = 0f,
    val touchscreenTrackpad: Boolean = PreferenceConfiguration.DEFAULT_TOUCHSCREEN_TRACKPAD,
    val onscreenController: Boolean = PreferenceConfiguration.ONSCREEN_CONTROLLER_DEFAULT,
    val hostAudio: Boolean = PreferenceConfiguration.DEFAULT_HOST_AUDIO,
    val mouseEmulation: Boolean = PreferenceConfiguration.DEFAULT_MOUSE_EMULATION,
    val vibrateOsc: Boolean = PreferenceConfiguration.DEFAULT_VIBRATE_OSC
)

class QuickSettingsHandler {
    var uiState by mutableStateOf(QuickSettingsUiState())
        private set

    fun onShowQuickSettings(
        currentFps: String,
        currentResolution: String,
        currentBitrate: Float,
        touchscreenTrackpad: Boolean,
        onscreenController: Boolean,
        hostAudio: Boolean,
        mouseEmulation: Boolean,
        vibrateOsc: Boolean
    ) {
        uiState = QuickSettingsUiState(
            showDialog = true,
            fps = currentFps,
            resolution = currentResolution,
            bitrate = currentBitrate,
            touchscreenTrackpad = touchscreenTrackpad,
            onscreenController = onscreenController,
            hostAudio = hostAudio,
            mouseEmulation = mouseEmulation,
            vibrateOsc = vibrateOsc
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

    fun onTouchscreenTrackpadChanged(context: Context, enabled: Boolean) {
        uiState = uiState.copy(touchscreenTrackpad = enabled)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(PreferenceConfiguration.TOUCHSCREEN_TRACKPAD_PREF_STRING, enabled)
        }
    }

    fun onOnscreenControllerChanged(context: Context, enabled: Boolean) {
        uiState = uiState.copy(onscreenController = enabled)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(PreferenceConfiguration.ONSCREEN_CONTROLLER_PREF_STRING, enabled)
        }
    }

    fun onHostAudioChanged(context: Context, enabled: Boolean) {
        uiState = uiState.copy(hostAudio = enabled)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(PreferenceConfiguration.HOST_AUDIO_PREF_STRING, enabled)
        }
    }

    fun onMouseEmulationChanged(context: Context, enabled: Boolean) {
        uiState = uiState.copy(mouseEmulation = enabled)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(PreferenceConfiguration.MOUSE_EMULATION_STRING, enabled)
        }
    }

    fun onVibrateOscChanged(context: Context, enabled: Boolean) {
        uiState = uiState.copy(vibrateOsc = enabled)
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(PreferenceConfiguration.VIBRATE_OSC_PREF_STRING, enabled)
        }
    }

    fun onDismissQuickSettings() {
        uiState = QuickSettingsUiState()
    }
}

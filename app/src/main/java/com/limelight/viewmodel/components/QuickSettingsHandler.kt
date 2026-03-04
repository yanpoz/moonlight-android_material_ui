package com.limelight.viewmodel.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class QuickSettingsUiState(
    val showDialog: Boolean = false,
)

class QuickSettingsHandler {
    var uiState by mutableStateOf(QuickSettingsUiState())
        private set

    fun onShowQuickSettings() {
        uiState = uiState.copy(showDialog = true)
    }

    fun onDismissQuickSettings() {
        uiState = QuickSettingsUiState()
    }
}

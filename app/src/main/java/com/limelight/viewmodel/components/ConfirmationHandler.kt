package com.limelight.viewmodel.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
data class ConfirmationDialogUiState(
    val showDialog: Boolean = false,
    val title: String = "",
    val text: String = "",
    val action: () -> Unit = {},
)

class ConfirmationHandler {
    var uiState by mutableStateOf(ConfirmationDialogUiState())
        private set

    fun confirmAction(title: String, text: String, action: () -> Unit = {}) {
        uiState = ConfirmationDialogUiState(true, title, text, action)
    }
    fun dismissDialog() {
        uiState = ConfirmationDialogUiState()
    }
}
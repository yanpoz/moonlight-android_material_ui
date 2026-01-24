package com.limelight.viewmodel.components

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.limelight.nvstream.http.NvApp

data class ConnectionDialogUiState(
    val showDialog: Boolean = false,
    val computerUuid: String? = null,
)

class ConnectionHandler(
    private val initiateConnection: (Context, String) -> Unit,
    private val launchApp: (Context, NvApp, String) -> Unit,
    private val cancelConnection: () -> Unit,
){
    var uiState by mutableStateOf(ConnectionDialogUiState())
        private set
    fun dismissDialog() {
        uiState = ConnectionDialogUiState()
    }
    fun onInitiateConnection(context: Context, computerUuid: String) {
        uiState = ConnectionDialogUiState(showDialog = true, computerUuid)
        initiateConnection(context, computerUuid)
    }
    fun onLaunchApp(context: Context, app: NvApp, computerUuid: String) {
        uiState = ConnectionDialogUiState(showDialog = true, computerUuid)
        launchApp(context, app, computerUuid)
    }
    fun onCancelConnection() {
        uiState = ConnectionDialogUiState()
        cancelConnection()
    }
}
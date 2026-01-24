package com.limelight.viewmodel.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
data class ManualComputerAddingUiState(
    val showDialog: Boolean = false,
    val inputIp: String = "",
)

class ManualComputerAddHandler(
    private val manualAddComputer: (String) -> Unit,
){
    var uiState by mutableStateOf(ManualComputerAddingUiState())
        private set
    fun onShowDialog() {
        uiState = uiState.copy(showDialog = true)
    }
    fun onDismissDialog() {
        uiState = ManualComputerAddingUiState()
    }
    fun onInputChanged(ip: String) {
        uiState = uiState.copy(inputIp = ip)
    }
    fun onManualAddComputer() {
        manualAddComputer(uiState.inputIp)
        onDismissDialog()
    }
}
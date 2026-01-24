package com.limelight.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limelight.computers.Computer
import com.limelight.nvstream.http.NvApp
import com.limelight.repository.ComputerRepository
import com.limelight.viewmodel.components.AppItemHandler
import com.limelight.viewmodel.components.ComputerItemHandler
import com.limelight.viewmodel.components.ConfirmationHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class ManualComputerAddingUiState(
    val showDialog: Boolean = false,
    val inputIp: String = "",
)
data class ConnectionDialogUiState(
    val showDialog: Boolean = false,
    val computerUuid: String? = null,
)

class ManualComputerAddHandler(
    private val computerRepository: ComputerRepository,)
{
    var uiState by mutableStateOf(ManualComputerAddingUiState())
        private set
    fun showDialog() {
        uiState = uiState.copy(showDialog = true)
    }
    fun dismissDialog() {
        uiState = ManualComputerAddingUiState()
    }
    fun onInputChanged(ip: String) {
        uiState = uiState.copy(inputIp = ip)
    }
    fun addComputer() {
        computerRepository.addComputer(uiState.inputIp)
        dismissDialog()
    }
}

class ConnectionHandler(
    private val computerRepository: ComputerRepository,)
{
    var dialog by mutableStateOf(ConnectionDialogUiState())
        private set
    private fun dismissDialog() {
        dialog = ConnectionDialogUiState()
    }
    fun initiateConnection(context: Context, computerUuid: String) {
        dialog = ConnectionDialogUiState(showDialog = true, computerUuid)
        computerRepository.initiateConnection(
            context, computerUuid, onAppLaunched = { dismissDialog() }
        )
    }
    fun launchApp(context: Context, app: NvApp, computerUuid: String) {
        dialog = ConnectionDialogUiState(showDialog = true, computerUuid)
        computerRepository.launchApp(
            context, app, computerUuid, onAppLaunched = { dismissDialog() }
        )
    }
    fun cancelConnection() {
        dialog = ConnectionDialogUiState()
        computerRepository.cancelConnection()
    }
}


class MainViewModel : ViewModel() {
    companion object {
        private const val APPS_POLL_DELAY_MS = 500L
        const val SETUP_GUIDE_URL =
            "https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/"
        const val TROUBLESHOOTING_URL =
            "https://github.com/moonlight-stream/moonlight-docs/wiki/Troubleshooting"
    }

    private val computerRepository = ComputerRepository()
    val computers: StateFlow<List<Computer>> = computerRepository.computers

    val confirmationHandler = ConfirmationHandler()
    val computerItemHandler = ComputerItemHandler(
        confirmationHandler = confirmationHandler,
        quitRunningApp = { context, computer ->
            computer.getRunningApp()?.let { app ->
                computerRepository.quitApp(context, app, computer.details.uuid)
            }
        },
        sendWakeOnLan = { context, computerUuid ->
            computerRepository.sendWakeOnLan(context, computerUuid)
        }
    )
    val appItemHandler = AppItemHandler(
        confirmationHandler = confirmationHandler,
        quitApp = { context, app, computerUuid ->
            computerRepository.quitApp(context, app, computerUuid)
        }
    )
    val manualComputerAddHandler = ManualComputerAddHandler(computerRepository)
    val connectionHandler = ConnectionHandler(computerRepository)

    var isRefreshing by mutableStateOf(false)

    //region Lifecycle & Service Management
    fun bindComputerManagerService(context: Context) {
        computerRepository.bindService(context)
    }
    fun unbindComputerManagerService(context: Context) {
        computerRepository.unbindService(context)
    }
    fun onUiResumed() {
        computerRepository.resumeComputerUpdates()
        computerRepository.pollAppsForActiveComputers()
    }
    fun onUiPaused() {
        computerRepository.pauseComputerUpdates()
    }
    //endregion

    //region Computer & App Actions
    fun updateComputerApps() {
        viewModelScope.launch {
            isRefreshing = true
            try {
                computerRepository.pollAppsForActiveComputers()
                delay(APPS_POLL_DELAY_MS)
            } finally {
                isRefreshing = false
            }
        }
    }
    //endregion
}

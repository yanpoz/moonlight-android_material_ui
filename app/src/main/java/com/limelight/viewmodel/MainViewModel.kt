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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class AppMenuUiState(
    val appId: Int? = null,
    val computerUuid: String? = null,
)
data class AppViewDetailsUiState(
    val showDialog: Boolean = false,
    val app: NvApp? = null,
)
data class ComputerMenuUiState(
    val computerUuid: String? = null,
)
data class ComputerViewDetailsUiState(
    val showDialog: Boolean = false,
    val computer: Computer? = null,
)
data class ManualComputerAddingUiState(
    val showDialog: Boolean = false,
    val inputIp: String = "",
)
data class ConnectionDialogUiState(
    val showDialog: Boolean = false,
    val computerUuid: String? = null,
)
data class ConfirmationDialogUiState(
    val showDialog: Boolean = false,
    val title: String = "",
    val text: String = "",
    val action: () -> Unit = {},
)

class ConfirmationHandler {
    var dialog by mutableStateOf(ConfirmationDialogUiState())
        private set
    fun confirmAction(title: String, text: String, action: () -> Unit = {}) {
        dialog = ConfirmationDialogUiState(true, title, text, action)
    }
    fun dismissDialog() {
        dialog = ConfirmationDialogUiState()
    }
}

class ComputerItemHandler(
    private val computerRepository: ComputerRepository,
    private val confirmationHandler: ConfirmationHandler,
) {
    var menu by mutableStateOf(ComputerMenuUiState())
        private set
    var viewDetails by mutableStateOf(ComputerViewDetailsUiState())
        private set
    fun openMenu(computerUuid: String) {
        menu = ComputerMenuUiState(computerUuid)
    }
    fun dismissMenu() {
        menu = ComputerMenuUiState()
    }
    fun onDetailsClicked(computer: Computer) {
        viewDetails = ComputerViewDetailsUiState(true, computer)
    }
    fun dismissDetailsDialog() {
        viewDetails = ComputerViewDetailsUiState()
    }
    fun onQuitRunningApp(context: Context, computer: Computer) {
        computer.getRunningApp()?.let { app ->
            confirmationHandler.confirmAction(
                title = "Quit ${app.appName}?",
                text = "Are you sure you want to quit ${app.appName}?",
                action = { computerRepository.quitApp(context, app, computer.details.uuid) }
            )
        }
    }
    fun onSendWakeOnLan(context: Context, computerUuid: String) {
        computerRepository.sendWakeOnLan(context, computerUuid)
    }
}

class AppItemHandler(
    private val computerRepository: ComputerRepository,
    private val confirmationHandler: ConfirmationHandler,
) {
    var menu by mutableStateOf(AppMenuUiState())
        private set
    var viewDetails by mutableStateOf(AppViewDetailsUiState())
        private set
    fun openMenu(appId: Int, computerUuid: String) {
        menu = AppMenuUiState(appId, computerUuid)
    }
    fun dismissMenu() {
        menu = AppMenuUiState()
    }
    fun onDetailsClicked(app: NvApp) {
        viewDetails = AppViewDetailsUiState(true, app)
    }
    fun dismissDetailsDialog() {
        viewDetails = AppViewDetailsUiState()
    }
    fun onQuitApp(context: Context, app: NvApp, computerUuid: String) {
        confirmationHandler.confirmAction(
            title = "Quit ${app.appName}?",
            text = "Are you sure you want to quit ${app.appName}?",
            action = { computerRepository.quitApp(context, app, computerUuid) }
        )
    }
}

class ManualComputerAddHandler(
    private val computerRepository: ComputerRepository,
) {
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
    private val computerRepository: ComputerRepository,
) {
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
    val computerItemHandler = ComputerItemHandler(computerRepository, confirmationHandler)
    val appItemHandler = AppItemHandler(computerRepository, confirmationHandler)
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

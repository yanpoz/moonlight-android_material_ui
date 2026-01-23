package com.limelight.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limelight.R
import com.limelight.nvstream.http.NvApp
import com.limelight.computers.Computer
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
    val computer: Computer? = null,
)
data class ComputerMenuUiState(
    val computerUuid: String? = null,
)
data class ComputerViewDetailsUiState(
    val showDialog: Boolean = false,
    val computer: Computer? = null,
)
data class ManualComputerAddingUiState(
    var showDialog: Boolean = false,
    var inputIp: String = "",
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


class MainViewModel : ViewModel() {
    companion object {
        private const val APPS_POLL_DELAY_MS = 500L
        const val SETUP_GUIDE_URL = "https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/"
        const val TROUBLESHOOTING_URL = "https://github.com/moonlight-stream/moonlight-docs/wiki/Troubleshooting"
    }

    // Computers with Apps Lists
    private val computerRepository = ComputerRepository()
    val computers: StateFlow<List<Computer>> = computerRepository.computers

    // UI States
    var appMenu by mutableStateOf(AppMenuUiState())
        private set
    var appViewDetails by mutableStateOf(AppViewDetailsUiState())
        private set
    var computerMenu by mutableStateOf(ComputerMenuUiState())
        private set
    var computerViewDetails by mutableStateOf(ComputerViewDetailsUiState())
        private set
    var manualComputerAdding by mutableStateOf(ManualComputerAddingUiState())
        private set
    var connectionDialog by mutableStateOf(ConnectionDialogUiState())
        private set
    var confirmationDialog by mutableStateOf(ConfirmationDialogUiState())
        private set

    // Other states
    var isRefreshing by mutableStateOf(false)

    fun onAppDetailsClicked(app: NvApp, computer: Computer) {
        appViewDetails = AppViewDetailsUiState(true, app, computer)
    }
    fun onComputerDetailsClicked(computer: Computer) {
        computerViewDetails = ComputerViewDetailsUiState(true, computer)
    }
    fun dismissAppDetailsDialog() {
        appViewDetails = AppViewDetailsUiState()
    }
    fun dismissComputerDetailsDialog() {
        computerViewDetails = ComputerViewDetailsUiState()
    }
    fun onComputerLongClick(computerUUID: String) {
        computerMenu = ComputerMenuUiState(computerUUID)
    }
    fun dismissComputerMenu() {
        computerMenu = ComputerMenuUiState()
    }
    fun onAppLongClick(appId: Int, computerUUID: String) {
        appMenu = AppMenuUiState(appId, computerUUID)
    }
    fun dismissAppMenu() {
        appMenu = AppMenuUiState()
    }
    fun confirmAction(title: String, text: String, action: () -> Unit = {}) {
        confirmationDialog = ConfirmationDialogUiState(true, title, text, action)
    }
    fun dismissConfirmationDialog() {
        confirmationDialog = ConfirmationDialogUiState()
    }
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
    fun addComputer(ipAddress: String) {
        computerRepository.addComputer(ipAddress)
        // Optionally, reset input IP and hide bottom sheet after attempting to add
        // inputIp = ""
        // showBottomSheet = false
    }
    fun onComputerInitiateConnection(context: Context, computerUuid: String) {
        val computer = computers.value.find { it.details.uuid == computerUuid }
        if (computer != null) {
            connectionDialog = ConnectionDialogUiState(showDialog = true, computerUuid)
            computerRepository.initiateConnection(
                context, computerUuid, onAppLaunched = { dismissConnectionDialog() }
            )
        }
    }
    fun onLaunchApp(context: Context, app: NvApp, computer: Computer) {
        connectionDialog = ConnectionDialogUiState(showDialog = true, computer.details.uuid)
        computerRepository.launchApp(
            context, app, computer.details.uuid, onAppLaunched = { dismissConnectionDialog() }
        )
    }
    fun onQuitApp(context: Context, app: NvApp, computerUuid: String) {
        confirmAction(
            title = "Quit ${app.appName}?",
            text = "Are you sure you want to quit ${app.appName}?",
            action = { computerRepository.quitApp(context, app, computerUuid) }
        )
    }
    fun onQuitRunningApp(context: Context, computer: Computer) {
        computer.getRunningApp()?.let {
            onQuitApp(context, it, computer.details.uuid)
        }
    }
    fun onSendWakeOnLan(context: Context, computerUuid: String) {
        computerRepository.sendWakeOnLan(context, computerUuid)
    }
    fun dismissConnectionDialog() {
        connectionDialog = ConnectionDialogUiState()
        computerRepository.cancelConnection()
    }

    @Composable
    fun getAppDetails(app: NvApp, computer: Computer): List<Pair<String, String>> {
        return listOf(
            stringResource(R.string.applist_details_id) to app.appId.toString(),
            "HDR Supported" to app.isHdrSupported.toString(),
            "Computer" to computer.details.name,
            "Computer ID" to computer.details.uuid
        )
    }
}

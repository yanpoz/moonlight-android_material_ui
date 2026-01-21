package com.limelight.viewmodel

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limelight.R
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import com.limelight.repository.Computer
import com.limelight.repository.ComputerRepository
import kotlinx.coroutines.delay
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
    val computer: Computer? = null,
)


class MainViewModel : ViewModel() {
    companion object {
        private const val APPS_POLL_DELAY_MS = 500L
    }

    // Computers with Apps Lists
    private val computerRepository = ComputerRepository()
    val computers: List<Computer> = computerRepository.computers

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

    // Other states
    var isRefreshing by mutableStateOf(false)
    var lastRunningAppId by mutableStateOf<Int?>(null)


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
    @Composable
    fun getAppDetails(app: NvApp, computer: Computer): List<Pair<String, String>> {
        return listOf(
            stringResource(R.string.applist_details_id) to app.appId.toString(),
            "HDR Supported" to app.isHdrSupported.toString(), //TODO
            "Computer" to computer.details.name,
            "Computer ID" to computer.details.uuid
        )
    }
    @Composable
    fun getComputerDetailsText(computer: Computer): List<Pair<String, String>> {
        val details = computer.details
        return listOfNotNull(
            "Name" to details.name,
            "UUID" to details.uuid,
            "State" to details.state.toString(),
            "Paired" to details.pairState.toString(),
            details.activeAddress?.let { "Active Address" to it.toString() },
            details.localAddress?.let { "Local Address" to it.toString() },
            details.remoteAddress?.let { "Remote Address" to it.toString() },
            details.manualAddress?.let { "Manual Address" to it.toString() },
            details.ipv6Address?.let { "IPv6 Address" to it.toString() },
            details.macAddress?.let { "MAC Address" to it },
            "HTTPS Port" to details.httpsPort.toString(),
            "External Port" to details.externalPort.toString(),
            "Running Game ID" to details.runningGameId.toString(),
            "NVIDIA Server" to details.nvidiaServer.toString(),
        )
    }
    fun onComputerLongPress(computerUUID: String) {
        computerMenu = ComputerMenuUiState(computerUUID)
    }
    fun dismissComputerMenu() {
        computerMenu = ComputerMenuUiState()
    }
    fun onAppLongPress(appId: Int, computerUUID: String) {
        appMenu = AppMenuUiState(appId, computerUUID)
    }
    fun dismissAppMenu() {
        appMenu = AppMenuUiState()
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
    fun updateApps() {
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
    fun onComputerConnect(context: Context, computerUUID: String) {
        val computer = computers.find { it.details.uuid == computerUUID }
        if (computer != null) {
            connectionDialog = ConnectionDialogUiState(showDialog = true, computer)
            computerRepository.initiateConnection(
                context, computerUUID, onAppLaunched = { dismissConnectionDialog() })
        }
    }
    fun onLaunchApp(context: Context, app: NvApp, computer: Computer) {
        connectionDialog = ConnectionDialogUiState(showDialog = true, computer)
        computerRepository.launchApp(
            context, app, computer, onAppLaunched = { dismissConnectionDialog() })
    }
    fun dismissConnectionDialog() {
        connectionDialog = ConnectionDialogUiState()
        computerRepository.cancelConnection()
    }
    @Composable
    fun getComputerAddressText(computer: Computer): String {
        return computer.details.activeAddress?.address
            ?: computer.details.localAddress?.address
            ?: computer.details.remoteAddress?.address
            ?: computer.details.manualAddress?.address
            ?: stringResource(R.string.error_unknown_host)
    }
    @Composable
    fun getPairStatusText(computer: Computer): String {
        return when (computer.details.pairState) {
            PairingManager.PairState.PAIRED -> "Pair status: Paired"
            PairingManager.PairState.NOT_PAIRED -> stringResource(R.string.scut_not_paired)
            PairingManager.PairState.PIN_WRONG -> stringResource(R.string.pair_incorrect_pin)
            PairingManager.PairState.FAILED -> stringResource(R.string.pair_fail)
            PairingManager.PairState.ALREADY_IN_PROGRESS -> stringResource(R.string.pairing)
            null -> stringResource(R.string.pair_fail) //TODO: Handle this better
        }
    }
    fun getPairPinText(computer: Computer): String {
        return when (computer.pairPin) {
            null -> "Generating PIN..." // TODO: add animation
            else -> "Pair PIN: ${computer.pairPin}"
        }
    }
    fun isComputerPaired(computer: Computer): Boolean {
        return computer.pairResult == PairingManager.PairState.PAIRED
    }
    @Composable
    fun getStatusColor(computer: Computer): Color {
        return when (computer.details.state) {
            ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.primary
            ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    @Composable
    fun getPairResultText(computer: Computer): String {
        return when (computer.pairResult) {
            null -> "NULL pair"
            PairingManager.PairState.ALREADY_IN_PROGRESS -> stringResource(R.string.pair_already_in_progress)
            PairingManager.PairState.PIN_WRONG -> stringResource(R.string.pair_incorrect_pin)
            PairingManager.PairState.FAILED -> {
                if (computer.details.runningGameId != 0) {
                    stringResource(R.string.pair_pc_ingame)
                } else {
                    stringResource(R.string.pair_fail)
                }
            }
            else -> "Pair Result: ${computer.pairResult}"
        }
    }
}

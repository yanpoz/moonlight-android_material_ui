package com.limelight.viewmodel

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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

class MainViewModel : ViewModel() {
    // Computers with Apps List
    private val computerRepository = ComputerRepository()
    val computers: List<Computer> = computerRepository.computers
    // States for Manual PC addition
    var showBottomSheet by mutableStateOf(false)
    var inputIp by mutableStateOf("")
    // States for Connection Dialog
    var showConnectionDialog by mutableStateOf(false)
    var computerForConnectUUID by mutableStateOf<String?>(null)
    val computerForConnect: Computer? by derivedStateOf {
        computerForConnectUUID?.let { uuid ->
            computers.find { it.details.uuid == uuid }
        }
    }
    // State for the computer expanded dropdown menu
    var computerUuidForComputerMenu by mutableStateOf<String?>(null)
        private set // Keep the setter private to enforce usage of open/dismiss methods
    // States for the expanded dropdown menu for apps
    var appIdForAppMenu by mutableStateOf<Int?>(null)
        private set
    var computerUuidForAppMenu by mutableStateOf<String?>(null)
        private set
    // States for App Details
    var showAppDetailsDialog by mutableStateOf(false)
    var appForAppDetails by mutableStateOf<NvApp?>(null)
    var computerForAppDetails by mutableStateOf<Computer?>(null)
    // States for Computer Details
    var showComputerDetailsDialog by mutableStateOf(false)
    var computerForComputerDetails by mutableStateOf<Computer?>(null)
    // Other states
    var isRefreshing by mutableStateOf(false)
    var lastRunningAppId by mutableStateOf<Int?>(null)

    fun onAppDetailsClicked(computer: Computer, app: NvApp) {
        appForAppDetails = app
        computerForAppDetails = computer
        showAppDetailsDialog = true
    }
    
    fun onComputerDetailsClicked(computer: Computer) {
        computerForComputerDetails = computer
        showComputerDetailsDialog = true
    }

    fun dismissAppDetailsDialog() {
        appForAppDetails = null
        computerForAppDetails = null
        showAppDetailsDialog = false
    }
    
    fun dismissComputerDetailsDialog() {
        computerForComputerDetails = null
        showComputerDetailsDialog = false
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
    fun getComputerDetails(computer: Computer): List<Pair<String, String>> {
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
        computerUuidForComputerMenu = computerUUID
    }

    fun dismissComputerMenu() {
        computerUuidForComputerMenu = null
    }

    fun onAppLongPress(computerUUID: String, appId: Int) {
        computerUuidForAppMenu = computerUUID
        appIdForAppMenu = appId
    }

    fun dismissAppMenu() {
        computerUuidForAppMenu = null
        appIdForAppMenu = null
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
                delay(500)
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
        showConnectionDialog = true
        computerForConnectUUID = computerUUID
        computerRepository.initiateConnection(context, computerUUID, onAppLaunched = { dismissConnectionDialog() })
    }

    fun onLaunchApp(context: Context, app: NvApp, computer: Computer) {
        computerRepository.launchApp(context, app, computer, onAppLaunched = { dismissConnectionDialog() })
    }

    fun dismissConnectionDialog() {
        showConnectionDialog = false
        computerForConnectUUID = null
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

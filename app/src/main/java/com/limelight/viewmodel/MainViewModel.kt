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
    var showBottomSheet by mutableStateOf(false)
    var inputIp by mutableStateOf("")

    var showConnectionDialog by mutableStateOf(false)
    private var selectedComputerUUID by mutableStateOf<String?>(null)
    val selectedComputer: Computer? by derivedStateOf {
        selectedComputerUUID?.let { uuid ->
            computers.find { it.details.uuid == uuid }
        }
    }
    private val computerRepository = ComputerRepository()
    val computers: List<Computer> = computerRepository.computers

    var isRefreshing by mutableStateOf(false)

    // State for the expanded dropdown menu
    var expandedMenuComputerUuid by mutableStateOf<String?>(null)
        private set // Keep the setter private to enforce usage of open/dismiss methods

    // State for the expanded dropdown menu for apps
    var expandedMenuAppId by mutableStateOf<Int?>(null)
        private set
    var expandedMenuComputerUuidForApp by mutableStateOf<String?>(null)
        private set

    var lastRunningAppId by mutableStateOf<Int?>(null)

    fun onComputerLongPress(computerUUID: String) {
        expandedMenuComputerUuid = computerUUID
    }

    fun dismissComputerMenu() {
        expandedMenuComputerUuid = null
    }

    fun onAppLongPress(computerUUID: String, appId: Int) {
        expandedMenuComputerUuidForApp = computerUUID
        expandedMenuAppId = appId
    }

    fun dismissAppMenu() {
        expandedMenuComputerUuidForApp = null
        expandedMenuAppId = null
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

    fun addComputer(context: Context, ipAddress: String) {
        computerRepository.addComputer(ipAddress)
        // Optionally, reset input IP and hide bottom sheet after attempting to add
        // inputIp = ""
        // showBottomSheet = false
    }

    fun onComputerClicked(computerUUID: String) {
        showConnectionDialog = true
        selectedComputerUUID = computerUUID
        computerRepository.initiateConnection(computerUUID)
    }

    fun launchApp(context: Context, app: NvApp, computer: Computer) {
        computerRepository.launchApp(context, app, computer)
    }

    fun dismissConnectionDialog() {
        showConnectionDialog = false
        selectedComputerUUID = null
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

    override fun onCleared() {
        super.onCleared()
        // It's good practice to ensure resources are released.
        // computerRepository.unbindService() should be called by the Activity/Fragment's onDestroy
        // If computerRepository had its own CoroutineScope that needs cancelling,
        // you might add a clear() method to the repository and call it here.
    }
}

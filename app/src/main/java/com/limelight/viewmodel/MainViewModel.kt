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
import com.limelight.R
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager
import com.limelight.repository.Computer
import com.limelight.repository.ComputerRepository

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

    fun bindComputerManagerService(context: Context) {
        computerRepository.bindService(context)
    }

    fun unbindComputerManagerService(context: Context) {
        computerRepository.unbindService(context)
    }

    fun onUiResumed() {
        computerRepository.resumeComputerUpdates()
    }

    fun onUiPaused() {
        computerRepository.pauseComputerUpdates()
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

    fun dismissConnectionDialog() {
        showConnectionDialog = false
        selectedComputerUUID = null
        computerRepository.cancelConnection()
    }

    fun getComputerAddressText(computer: Computer): String {
        return computer.details.activeAddress?.address
            ?: computer.details.localAddress?.address
            ?: computer.details.remoteAddress?.address
            ?: computer.details.manualAddress?.address
            ?: "Unknown Address"
    }

    fun getPairStatusText(computer: Computer): String {
        return when (computer.details.pairState) {
            PairingManager.PairState.PAIRED -> "Pair status: Paired"
            PairingManager.PairState.NOT_PAIRED -> "Pair status: Not Paired"
            PairingManager.PairState.PIN_WRONG -> "Pair status: PIN Incorrect"
            PairingManager.PairState.FAILED -> "Pair status: Pairing Failed"
            PairingManager.PairState.ALREADY_IN_PROGRESS -> "Pair status: Pairing in Progress"
            null -> "Pair status: Unknown"
        }
    }

    fun getPairPinText(computer: Computer): String {
        return when (computer.pairPin) {
            null -> "Generating PIN..."
            else -> "Pair PIN: ${computer.pairPin}"
        }
    }

    fun getRawAppListText(computer: Computer): String {
        return when (computer.details.rawAppList) {
            null -> "Fetching App List..."
            else -> "App List: ${computer.details.rawAppList}"
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
        // It'''s good practice to ensure resources are released.
        // computerRepository.unbindService() should be called by the Activity/Fragment'''s onDestroy
        // If computerRepository had its own CoroutineScope that needs cancelling,
        // you might add a clear() method to the repository and call it here.
    }
}

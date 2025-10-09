package com.limelight.viewmodel

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.limelight.R // Import R class for resources
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.repository.ComputerRepository // Import the repository

class MainViewModel : ViewModel() {
    // UI properties
    var showBottomSheet by mutableStateOf(false)
    var inputIp by mutableStateOf("")

    var showConnectionDialog by mutableStateOf(false)
    private var selectedComputerUUID by mutableStateOf<String?>(null)
    val computerForConnect: ComputerDetails? by derivedStateOf {
        selectedComputerUUID?.let { uuid ->
            computers.find { it.uuid == uuid }
        }
    }
    val connectionMsg: Int? by derivedStateOf {
        when {
            computerForConnect == null -> null
            computerForConnect?.state == ComputerDetails.State.OFFLINE -> R.string.pair_pc_offline
            computerForConnect?.activeAddress == null -> R.string.error_unknown_host
            !computerRepository.isServiceConnected -> R.string.error_manager_not_running
            else -> R.string.conn_error_title
        }
    }
    private val computerRepository = ComputerRepository()
    val computers: List<ComputerDetails> = computerRepository.computers

    // Delegate service binding and unbinding to the repository
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

    // Delegate adding a computer to the repository
    fun addComputer(context: Context, ipAddress: String) {
        computerRepository.addComputer(context, ipAddress)
        // Optionally, reset input IP and hide bottom sheet after attempting to add
        // inputIp = ""
        // showBottomSheet = false
    }

    fun onComputerClicked(computer: ComputerDetails) {
        selectedComputerUUID = computer.uuid
        showConnectionDialog = true
        // connectionMsg will be derived based on the new computerForConnect
    }

    fun dismissComputerDialog() {
        showConnectionDialog = false
        selectedComputerUUID = null // Clear the selected name
        // computerForConnect will become null, and connectionMsg will update accordingly
    }

    override fun onCleared() {
        super.onCleared()
        // It's good practice to ensure resources are released.
        // computerRepository.unbindService() should be called by the Activity/Fragment's onDestroy
        // If computerRepository had its own CoroutineScope that needs cancelling,
        // you might add a clear() method to the repository and call it here.
    }
}

package com.limelight.viewmodel

import android.content.Context
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
    var computerForConnect by mutableStateOf<ComputerDetails?>(null)
    var connectionErrorMsg by mutableStateOf<Int?>(null) // Changed to Int? for resource ID

    // Instantiate the ComputerRepository
    private val computerRepository = ComputerRepository()

    // Expose the computers list from the repository
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
        computerForConnect = computer // Set computer details first
        showConnectionDialog = true

        connectionErrorMsg = if (computer.state == ComputerDetails.State.OFFLINE || computer.activeAddress == null) {
            R.string.pair_pc_offline // Assign resource ID
        } else {
            null // Clear previous error messages
        }
    }

    fun dismissComputerDialog() {
        showConnectionDialog = false
        computerForConnect = null
        connectionErrorMsg = null // Clear error message on dismiss
    }

    override fun onCleared() {
        super.onCleared()
        // It's good practice to ensure resources are released.
        // computerRepository.unbindService() should be called by the Activity/Fragment's onDestroy
        // If computerRepository had its own CoroutineScope that needs cancelling,
        // you might add a clear() method to the repository and call it here.
    }
}

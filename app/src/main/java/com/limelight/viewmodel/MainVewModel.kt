package com.limelight.viewmodel

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.repository.ComputerRepository
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {
    var showBottomSheet by mutableStateOf(false)
    var inputIp by mutableStateOf("")

    var showConnectionDialog by mutableStateOf(false)
    private var selectedComputerUUID by mutableStateOf<String?>(null)
    val selectedComputer: ComputerDetails? by derivedStateOf {
        selectedComputerUUID?.let { uuid ->
            computers.find { it.uuid == uuid }
        }
    }
    private val computerRepository = ComputerRepository()
    val computers: List<ComputerDetails> = computerRepository.computers

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
        computerRepository.addComputer(context, ipAddress)
        // Optionally, reset input IP and hide bottom sheet after attempting to add
        // inputIp = ""
        // showBottomSheet = false
    }

    fun onComputerClicked(computer: ComputerDetails) {
        showConnectionDialog = true
        selectedComputerUUID = computer.uuid
        computerRepository.initiateConnection(computer)
    }

    fun dismissComputerDialog() {
        showConnectionDialog = false
        selectedComputerUUID = null
        computerRepository.cancelConnection()
    }

    override fun onCleared() {
        super.onCleared()
        // It's good practice to ensure resources are released.
        // computerRepository.unbindService() should be called by the Activity/Fragment's onDestroy
        // If computerRepository had its own CoroutineScope that needs cancelling,
        // you might add a clear() method to the repository and call it here.
    }
}

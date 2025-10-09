package com.limelight.viewmodel

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limelight.R
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.repository.ComputerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var pairingMessage by mutableStateOf("")
    private var connectionJob: Job? = null
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

    fun addComputer(context: Context, ipAddress: String) {
        computerRepository.addComputer(context, ipAddress)
        // Optionally, reset input IP and hide bottom sheet after attempting to add
        // inputIp = ""
        // showBottomSheet = false
    }

    fun onComputerClicked(computer: ComputerDetails, context: Context) {
        selectedComputerUUID = computer.uuid
        showConnectionDialog = true
        initiateConnection(computer, context)
    }

    private fun initiateConnection(computer: ComputerDetails, context: Context) {
        connectionJob?.cancel()

        connectionJob = viewModelScope.launch {
            pairingMessage = context.getString(R.string.pairing)
            while (true) {
                val currentComputer = selectedComputer ?: break
                if (currentComputer.activeAddress != null &&
                    currentComputer.state != ComputerDetails.State.OFFLINE &&
                    computerRepository.isServiceConnected
                    ) {
                    pairingMessage = computerRepository.pairComputer(context, currentComputer)
                    break
                }
                delay(1000L)
            }
        }
    }

    fun dismissComputerDialog() {
        showConnectionDialog = false
        connectionJob?.cancel()
        selectedComputerUUID = null
        pairingMessage = ""        
    }

    override fun onCleared() {
        super.onCleared()
        // It's good practice to ensure resources are released.
        // computerRepository.unbindService() should be called by the Activity/Fragment's onDestroy
        // If computerRepository had its own CoroutineScope that needs cancelling,
        // you might add a clear() method to the repository and call it here.
    }
}

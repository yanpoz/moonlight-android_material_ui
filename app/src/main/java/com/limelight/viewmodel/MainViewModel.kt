package com.limelight.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limelight.computers.Computer
import com.limelight.repository.ComputerRepository
import com.limelight.viewmodel.components.AppItemHandler
import com.limelight.viewmodel.components.ComputerItemHandler
import com.limelight.viewmodel.components.ConfirmationHandler
import com.limelight.viewmodel.components.ConnectionHandler
import com.limelight.viewmodel.components.ManualComputerAddHandler
import com.limelight.viewmodel.components.QuickSettingsHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class MainViewModel(
    private val computerRepository: ComputerRepository = ComputerRepository()) : ViewModel() {
    companion object {
        private const val APPS_POLL_DELAY_MS = 500L
        const val SETUP_GUIDE_URL =
            "https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/"
        const val TROUBLESHOOTING_URL =
            "https://github.com/moonlight-stream/moonlight-docs/wiki/Troubleshooting"
    }

    open val computers: StateFlow<List<Computer>> = computerRepository.computers
    open val uniqueId: StateFlow<String?> = computerRepository.uniqueId

    val confirmationHandler = ConfirmationHandler()
    val computerItemHandler = ComputerItemHandler(
        confirmationHandler = confirmationHandler,
        quitRunningApp = { context, computer ->
            computer.getRunningApp()?.let { app ->
                computerRepository.quitApp(context, app, computer.details.uuid)
            }
        },
        sendWakeOnLan = { context, computerUuid ->
            computerRepository.sendWakeOnLan(context, computerUuid)
        }
    )
    val appItemHandler = AppItemHandler(
        confirmationHandler = confirmationHandler,
        quitApp = { context, app, computerUuid ->
            computerRepository.quitApp(context, app, computerUuid)
        }
    )
    val manualComputerAddHandler = ManualComputerAddHandler(
        manualAddComputer = { ipAddress ->
            computerRepository.addComputer(ipAddress)
        },
    )
    val connectionHandler = ConnectionHandler(
        initiateConnection = { context, computerUuid ->
            computerRepository.initiateConnection(context, computerUuid)
        },
        launchApp = { context, app, computerUuid ->
            computerRepository.launchApp(context, app, computerUuid)
        },
        cancelConnection = {
            computerRepository.cancelConnection()
        }
    )
    val quickSettingsHandler = QuickSettingsHandler()

    private val _isRefreshing = MutableStateFlow(false)
    open val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            computerRepository.connectionStatus.collect { status ->
                if (status == ComputerRepository.ConnectionStatus.SUCCESS ||
                    status == ComputerRepository.ConnectionStatus.CANCELED
                ) {
                    connectionHandler.dismissDialog()
                }
            }
        }
    }

    //region Lifecycle & Service Management
    open fun bindComputerManagerService(context: Context) {
        computerRepository.bindService(context)
    }
    open fun unbindComputerManagerService(context: Context) {
        computerRepository.unbindService(context)
    }
    open fun onUiResumed() {
        computerRepository.resumeComputerUpdates()
        computerRepository.pollAppsForActiveComputers()
    }
    open fun onUiPaused() {
        computerRepository.pauseComputerUpdates()
    }
    //endregion

    //region Computer & App Actions
    open fun updateComputerApps() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                computerRepository.pollAppsForActiveComputers()
                delay(APPS_POLL_DELAY_MS)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    //endregion
}

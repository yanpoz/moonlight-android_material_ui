package com.limelight.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limelight.computers.Computer
import com.limelight.nvstream.http.NvApp
import com.limelight.repository.ComputerRepository
import com.limelight.viewmodel.components.AppItemHandler
import com.limelight.viewmodel.components.AppMenuUiState
import com.limelight.viewmodel.components.AppViewDetailsUiState
import com.limelight.viewmodel.components.ComputerItemHandler
import com.limelight.viewmodel.components.ComputerMenuUiState
import com.limelight.viewmodel.components.ComputerViewDetailsUiState
import com.limelight.viewmodel.components.ConfirmationDialogUiState
import com.limelight.viewmodel.components.ConfirmationHandler
import com.limelight.viewmodel.components.ConnectionDialogUiState
import com.limelight.viewmodel.components.ConnectionHandler
import com.limelight.viewmodel.components.ManualComputerAddHandler
import com.limelight.viewmodel.components.ManualComputerAddingUiState
import com.limelight.viewmodel.components.NetworkTestUiState
import com.limelight.viewmodel.components.QuickSettingsHandler
import com.limelight.viewmodel.components.QuickSettingsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI State for the Main Screen.
 * Contains all the data required to render the MainScreenContent.
 */
data class MainScreenUiState(
    val computers: List<Computer> = emptyList(),
    val isRefreshing: Boolean = false,
    val uniqueId: String? = null,
    val networkTestStatus: ComputerRepository.NetworkTestStatus = ComputerRepository.NetworkTestStatus.Idle,
    val manualComputerAddUiState: ManualComputerAddingUiState = ManualComputerAddingUiState(),
    val connectionUiState: ConnectionDialogUiState = ConnectionDialogUiState(),
    val appMenuUiState: AppMenuUiState = AppMenuUiState(),
    val appViewDetailsUiState: AppViewDetailsUiState = AppViewDetailsUiState(),
    val computerMenuUiState: ComputerMenuUiState = ComputerMenuUiState(),
    val computerViewDetailsUiState: ComputerViewDetailsUiState = ComputerViewDetailsUiState(),
    val networkTestUiState: NetworkTestUiState = NetworkTestUiState(),
    val confirmationUiState: ConfirmationDialogUiState = ConfirmationDialogUiState(),
    val quickSettingsUiState: QuickSettingsUiState = QuickSettingsUiState()
)

/**
 * Actions for the Main Screen.
 * Holds all event handlers to keep the screen composable clean.
 */
data class MainScreenActions(
    val onSettingsClick: () -> Unit = {},
    val onHelpClick: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    // Manual Computer Add
    val onShowManualAddDialog: () -> Unit = {},
    val onManualComputerAddInputChanged: (String) -> Unit = {},
    val onManualComputerAddConfirm: () -> Unit = {},
    val onManualComputerAddDismiss: () -> Unit = {},
    // Quick Settings
    val onShowQuickSettings: () -> Unit = {},
    val onQuickSettingsFpsChanged: (String) -> Unit = {},
    val onQuickSettingsResolutionChanged: (String) -> Unit = {},
    val onQuickSettingsBitrateChanged: (Float) -> Unit = {},
    val onQuickSettingsTouchscreenTrackpadChanged: (Boolean) -> Unit = {},
    val onQuickSettingsOnscreenControllerChanged: (Boolean) -> Unit = {},
    val onQuickSettingsHostAudioChanged: (Boolean) -> Unit = {},
    val onQuickSettingsMouseEmulationChanged: (Boolean) -> Unit = {},
    val onQuickSettingsVibrateOscChanged: (Boolean) -> Unit = {},
    val onQuickSettingsDismiss: () -> Unit = {},
    // Connection
    val onConnectionInitiate: (String) -> Unit = {},
    val onConnectionCancel: () -> Unit = {},
    val onLaunchApp: (NvApp, String) -> Unit = { _, _ -> },
    // App Item
    val onAppMenuOpen: (Int, String) -> Unit = { _, _ -> },
    val onAppMenuDismiss: () -> Unit = {},
    val onAppQuit: (NvApp, String) -> Unit = { _, _ -> },
    val onAppDetailsClick: (NvApp) -> Unit = {},
    val onAppDetailsDismiss: () -> Unit = {},
    val onAppMoveUp: (String, Int) -> Unit = { _, _ -> },
    val onAppMoveDown: (String, Int) -> Unit = { _, _ -> },
    // Computer Item
    val onComputerMenuOpen: (String) -> Unit = {},
    val onComputerMenuDismiss: () -> Unit = {},
    val onComputerDetailsClick: (Computer) -> Unit = {},
    val onComputerDetailsDismiss: () -> Unit = {},
    val onComputerQuitRunningApp: (Computer) -> Unit = {},
    val onComputerWakeOnLan: (String) -> Unit = {},
    val onComputerMoveUp: (String) -> Unit = {},
    val onComputerMoveDown: (String) -> Unit = {},
    val onComputerDelete: (Computer) -> Unit = {},
    val onComputerTestNetwork: () -> Unit = {},
    val onComputerDismissNetworkTest: () -> Unit = {},
    // Confirmation
    val onConfirmationConfirm: () -> Unit = {},
    val onConfirmationDismiss: () -> Unit = {},
)

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
        .map { list ->
            list.sortedWith(
                compareBy<Computer> { it.position }.thenBy { it.details.name })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    open val uniqueId: StateFlow<String?> = computerRepository.uniqueId
    open val networkTestStatus: StateFlow<ComputerRepository.NetworkTestStatus> =
        computerRepository.networkTestStatus

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
        },
        moveUp = { computerUuid ->
            computerRepository.moveComputerUp(computerUuid)
        },
        moveDown = { computerUuid ->
            computerRepository.moveComputerDown(computerUuid)
        },
        deleteComputer = { computerUuid ->
            computerRepository.deleteComputer(computerUuid)
        },
        testNetwork = { context ->
            computerRepository.testNetwork(context)
        },
        dismissNetworkTest = {
            computerRepository.dismissNetworkTest()
        }
    )
    val appItemHandler = AppItemHandler(
        confirmationHandler = confirmationHandler,
        quitApp = { context, app, computerUuid ->
            computerRepository.quitApp(context, app, computerUuid)
        },
        moveUp = { computerUuid, appId ->
            computerRepository.moveAppUp(computerUuid, appId)
        },
        moveDown = { computerUuid, appId ->
            computerRepository.moveAppDown(computerUuid, appId)
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

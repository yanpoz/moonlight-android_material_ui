package com.limelight.ui

import androidx.compose.runtime.Composable
import com.limelight.ui.components.dialogs.AppDetailsDialog
import com.limelight.ui.components.dialogs.ComputerDetailsDialog
import com.limelight.ui.components.dialogs.ConfirmationDialog
import com.limelight.ui.components.dialogs.ConnectionDialog
import com.limelight.ui.components.dialogs.ManualComputerAddDialog
import com.limelight.ui.components.dialogs.NetworkTestDialog
import com.limelight.ui.components.dialogs.QuickSettingsDialog
import com.limelight.viewmodel.MainScreenActions
import com.limelight.viewmodel.MainScreenUiState

@Composable
fun MainScreenDialogs(
    uiState: MainScreenUiState,
    actions: MainScreenActions
) {
    if (uiState.manualComputerAddUiState.showDialog) {
        ManualComputerAddDialog(
            inputIp = uiState.manualComputerAddUiState.inputIp,
            onInputIpChange = actions.onManualComputerAddInputChanged,
            onAddComputer = actions.onManualComputerAddConfirm,
            onDismiss = actions.onManualComputerAddDismiss,
        )
    }

    if (uiState.connectionUiState.showDialog && uiState.connectionUiState.computerUuid != null) {
        val computer = uiState.computers.find { it.details.uuid == uiState.connectionUiState.computerUuid }
        if (computer != null) {
            ConnectionDialog(
                computer,
                onConnect = { actions.onConnectionInitiate(computer.details.uuid) },
                onDismiss = actions.onConnectionCancel
            )
        }
    }

    if (uiState.appViewDetailsUiState.showDialog) {
        uiState.appViewDetailsUiState.app?.let { app ->
            AppDetailsDialog(
                app = app,
                onDismiss = actions.onAppDetailsDismiss,
            )
        }
    }

    if (uiState.computerViewDetailsUiState.showDialog) {
        uiState.computerViewDetailsUiState.computer?.let { computer ->
            ComputerDetailsDialog(
                computer = computer,
                onDismiss = actions.onComputerDetailsDismiss,
            )
        }
    }

    if (uiState.confirmationUiState.showDialog) {
        ConfirmationDialog(
            title = uiState.confirmationUiState.title,
            text = uiState.confirmationUiState.text,
            onConfirm = actions.onConfirmationConfirm,
            onDismiss = actions.onConfirmationDismiss
        )
    }

    if (uiState.quickSettingsUiState.showDialog) {
        QuickSettingsDialog(
            fps = uiState.quickSettingsUiState.fps,
            onFpsChanged = actions.onQuickSettingsFpsChanged,
            resolution = uiState.quickSettingsUiState.resolution,
            onResolutionChanged = actions.onQuickSettingsResolutionChanged,
            bitrate = uiState.quickSettingsUiState.bitrate,
            onBitrateChanged = actions.onQuickSettingsBitrateChanged,
            touchscreenTrackpad = uiState.quickSettingsUiState.touchscreenTrackpad,
            onTouchscreenTrackpadChanged = actions.onQuickSettingsTouchscreenTrackpadChanged,
            onscreenController = uiState.quickSettingsUiState.onscreenController,
            onOnscreenControllerChanged = actions.onQuickSettingsOnscreenControllerChanged,
            hostAudio = uiState.quickSettingsUiState.hostAudio,
            onHostAudioChanged = actions.onQuickSettingsHostAudioChanged,
            mouseEmulation = uiState.quickSettingsUiState.mouseEmulation,
            onMouseEmulationChanged = actions.onQuickSettingsMouseEmulationChanged,
            vibrateOsc = uiState.quickSettingsUiState.vibrateOsc,
            onVibrateOscChanged = actions.onQuickSettingsVibrateOscChanged,
            onDismiss = actions.onQuickSettingsDismiss
        )
    }

    if (uiState.networkTestUiState.showDialog) {
        NetworkTestDialog(
            networkTestStatus = uiState.networkTestStatus,
            onDismiss = actions.onComputerDismissNetworkTest
        )
    }
}

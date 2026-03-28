package com.limelight.ui

import androidx.compose.runtime.Composable
import com.limelight.ui.components.dialogs.AppDetailsDialog
import com.limelight.ui.components.dialogs.ComputerDetailsDialog
import com.limelight.ui.components.dialogs.ConfirmationDialog
import com.limelight.ui.components.dialogs.ConnectionDialog
import com.limelight.ui.components.dialogs.ManualComputerAddDialog
import com.limelight.ui.components.dialogs.NetworkTestDialog
import com.limelight.ui.components.dialogs.QuickSettingsDialog

@Composable
fun MainScreenDialogs(
    state: MainScreenUiState,
    actions: MainScreenActions
) {
    if (state.manualComputerAddUiState.showDialog) {
        ManualComputerAddDialog(
            inputIp = state.manualComputerAddUiState.inputIp,
            onInputIpChange = actions.onManualComputerAddInputChanged,
            onAddComputer = actions.onManualComputerAddConfirm,
            onDismiss = actions.onManualComputerAddDismiss,
        )
    }

    if (state.connectionUiState.showDialog && state.connectionUiState.computerUuid != null) {
        val computer = state.computers.find { it.details.uuid == state.connectionUiState.computerUuid }
        if (computer != null) {
            ConnectionDialog(
                computer,
                onConnect = { actions.onConnectionInitiate(computer.details.uuid) },
                onDismiss = actions.onConnectionCancel
            )
        }
    }

    if (state.appViewDetailsUiState.showDialog) {
        state.appViewDetailsUiState.app?.let { app ->
            AppDetailsDialog(
                app = app,
                onDismiss = actions.onAppDetailsDismiss,
            )
        }
    }

    if (state.computerViewDetailsUiState.showDialog) {
        state.computerViewDetailsUiState.computer?.let { computer ->
            ComputerDetailsDialog(
                computer = computer,
                onDismiss = actions.onComputerDetailsDismiss,
            )
        }
    }

    if (state.confirmationUiState.showDialog) {
        ConfirmationDialog(
            title = state.confirmationUiState.title,
            text = state.confirmationUiState.text,
            onConfirm = actions.onConfirmationConfirm,
            onDismiss = actions.onConfirmationDismiss
        )
    }

    if (state.quickSettingsUiState.showDialog) {
        QuickSettingsDialog(
            fps = state.quickSettingsUiState.fps,
            onFpsChanged = actions.onQuickSettingsFpsChanged,
            resolution = state.quickSettingsUiState.resolution,
            onResolutionChanged = actions.onQuickSettingsResolutionChanged,
            bitrate = state.quickSettingsUiState.bitrate,
            onBitrateChanged = actions.onQuickSettingsBitrateChanged,
            touchscreenTrackpad = state.quickSettingsUiState.touchscreenTrackpad,
            onTouchscreenTrackpadChanged = actions.onQuickSettingsTouchscreenTrackpadChanged,
            onscreenController = state.quickSettingsUiState.onscreenController,
            onOnscreenControllerChanged = actions.onQuickSettingsOnscreenControllerChanged,
            hostAudio = state.quickSettingsUiState.hostAudio,
            onHostAudioChanged = actions.onQuickSettingsHostAudioChanged,
            mouseEmulation = state.quickSettingsUiState.mouseEmulation,
            onMouseEmulationChanged = actions.onQuickSettingsMouseEmulationChanged,
            vibrateOsc = state.quickSettingsUiState.vibrateOsc,
            onVibrateOscChanged = actions.onQuickSettingsVibrateOscChanged,
            onDismiss = actions.onQuickSettingsDismiss
        )
    }

    if (state.networkTestUiState.showDialog) {
        NetworkTestDialog(
            networkTestStatus = state.networkTestStatus,
            onDismiss = actions.onComputerDismissNetworkTest
        )
    }
}

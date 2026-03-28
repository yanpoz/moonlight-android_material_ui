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
    // Manual Computer Add
    onManualComputerAddInputChanged: (String) -> Unit,
    onManualComputerAddConfirm: () -> Unit,
    onManualComputerAddDismiss: () -> Unit,
    // Quick Settings
    onQuickSettingsFpsChanged: (String) -> Unit,
    onQuickSettingsResolutionChanged: (String) -> Unit,
    onQuickSettingsBitrateChanged: (Float) -> Unit,
    onQuickSettingsTouchscreenTrackpadChanged: (Boolean) -> Unit,
    onQuickSettingsOnscreenControllerChanged: (Boolean) -> Unit,
    onQuickSettingsHostAudioChanged: (Boolean) -> Unit,
    onQuickSettingsMouseEmulationChanged: (Boolean) -> Unit,
    onQuickSettingsVibrateOscChanged: (Boolean) -> Unit,
    onQuickSettingsDismiss: () -> Unit,
    // Connection
    onConnectionInitiate: (String) -> Unit,
    onConnectionCancel: () -> Unit,
    // Details
    onAppDetailsDismiss: () -> Unit,
    onComputerDetailsDismiss: () -> Unit,
    // Network Test
    onComputerDismissNetworkTest: () -> Unit,
    // Confirmation
    onConfirmationConfirm: () -> Unit,
    onConfirmationDismiss: () -> Unit,
) {
    if (state.manualComputerAddUiState.showDialog) {
        ManualComputerAddDialog(
            inputIp = state.manualComputerAddUiState.inputIp,
            onInputIpChange = onManualComputerAddInputChanged,
            onAddComputer = onManualComputerAddConfirm,
            onDismiss = onManualComputerAddDismiss,
        )
    }

    if (state.connectionUiState.showDialog && state.connectionUiState.computerUuid != null) {
        val computer = state.computers.find { it.details.uuid == state.connectionUiState.computerUuid }
        if (computer != null) {
            ConnectionDialog(
                computer,
                onConnect = { onConnectionInitiate(computer.details.uuid) },
                onDismiss = onConnectionCancel
            )
        }
    }

    if (state.appViewDetailsUiState.showDialog) {
        state.appViewDetailsUiState.app?.let { app ->
            AppDetailsDialog(
                app = app,
                onDismiss = onAppDetailsDismiss,
            )
        }
    }

    if (state.computerViewDetailsUiState.showDialog) {
        state.computerViewDetailsUiState.computer?.let { computer ->
            ComputerDetailsDialog(
                computer = computer,
                onDismiss = onComputerDetailsDismiss,
            )
        }
    }

    if (state.confirmationUiState.showDialog) {
        ConfirmationDialog(
            title = state.confirmationUiState.title,
            text = state.confirmationUiState.text,
            onConfirm = onConfirmationConfirm,
            onDismiss = onConfirmationDismiss
        )
    }

    if (state.quickSettingsUiState.showDialog) {
        QuickSettingsDialog(
            fps = state.quickSettingsUiState.fps,
            onFpsChanged = onQuickSettingsFpsChanged,
            resolution = state.quickSettingsUiState.resolution,
            onResolutionChanged = onQuickSettingsResolutionChanged,
            bitrate = state.quickSettingsUiState.bitrate,
            onBitrateChanged = onQuickSettingsBitrateChanged,
            touchscreenTrackpad = state.quickSettingsUiState.touchscreenTrackpad,
            onTouchscreenTrackpadChanged = onQuickSettingsTouchscreenTrackpadChanged,
            onscreenController = state.quickSettingsUiState.onscreenController,
            onOnscreenControllerChanged = onQuickSettingsOnscreenControllerChanged,
            hostAudio = state.quickSettingsUiState.hostAudio,
            onHostAudioChanged = onQuickSettingsHostAudioChanged,
            mouseEmulation = state.quickSettingsUiState.mouseEmulation,
            onMouseEmulationChanged = onQuickSettingsMouseEmulationChanged,
            vibrateOsc = state.quickSettingsUiState.vibrateOsc,
            onVibrateOscChanged = onQuickSettingsVibrateOscChanged,
            onDismiss = onQuickSettingsDismiss
        )
    }

    if (state.networkTestUiState.showDialog) {
        NetworkTestDialog(
            networkTestStatus = state.networkTestStatus,
            onDismiss = onComputerDismissNetworkTest
        )
    }
}

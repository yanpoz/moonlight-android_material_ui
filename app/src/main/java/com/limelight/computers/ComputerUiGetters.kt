package com.limelight.computers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.limelight.R
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import com.limelight.viewmodel.components.ComputerItemUiState


@Composable
fun Computer.toUiState(): ComputerItemUiState {
    val address = details.activeAddress?.toString()
        ?: details.localAddress?.toString()
        ?: details.remoteAddress?.toString()
        ?: details.manualAddress?.toString()
        ?: "UNKNOWN"

    val actionText = when (details.state) {
        ComputerDetails.State.OFFLINE -> "Send Wake-On-LAN"
        ComputerDetails.State.UNKNOWN -> "Send Wake-On-LAN"
        else -> {
            if (details.pairState != PairingManager.PairState.PAIRED) {
                "Pair to PC"
            } else {
                val runningApp = getRunningApp()
                if (runningApp != null) {
                    "Connect to: ${runningApp.appName}"
                } else {
                    "Connect to Desktop"
                }
            }
        }
    }

    val networkStateText = when (details.state) {
        ComputerDetails.State.ONLINE -> "Online"
        ComputerDetails.State.OFFLINE -> "Offline"
        ComputerDetails.State.UNKNOWN -> "Unknown"
        else -> "NULL"
    }

    val pairStatusText = when (details.pairState) {
        PairingManager.PairState.PAIRED -> "Paired"
        PairingManager.PairState.NOT_PAIRED -> "Not paired"
        PairingManager.PairState.PIN_WRONG -> stringResource(R.string.pair_incorrect_pin)
        PairingManager.PairState.FAILED -> stringResource(R.string.pair_fail)
        PairingManager.PairState.ALREADY_IN_PROGRESS -> stringResource(R.string.pairing)
        null -> null
    }

    // Rounded pill with text
    val statusText_old = if (details.state == ComputerDetails.State.UNKNOWN) {
        "Connecting..."
    } else if (pairStatusText != null) {
        "$networkStateText • $pairStatusText"
    } else {
        networkStateText
    }

    val statusText = when (state) {
        Computer.State.STREAMING -> "Streaming"
        Computer.State.READY_TO_CONNECT -> "Ready to\nconnect"
        Computer.State.READY_TO_PAIR -> "Ready\nto pair"
        Computer.State.OFFLINE -> "Offline"
        Computer.State.CONNECTING -> "Connecting"
        Computer.State.ERROR -> "Error"
    }


    val cardColor = when (state) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.primaryContainer
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.ERROR -> MaterialTheme.colorScheme.secondaryContainer
    }

    val statusColor = when (state) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.tertiary
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.primary
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.tertiary
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondary
        Computer.State.ERROR -> MaterialTheme.colorScheme.error
    }

    val statusTextColor = when (state) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.onTertiary
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.onPrimary
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.onTertiary
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.onSecondary
        Computer.State.ERROR -> MaterialTheme.colorScheme.error
    }

    val actionTextColor = when (details.state) {
        ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.UNKNOWN -> MaterialTheme.colorScheme.secondary
    }

    val statusShapeState = when (state) {
        Computer.State.STREAMING -> com.limelight.viewmodel.components.StatusShapeState.Orbit
        Computer.State.READY_TO_CONNECT -> com.limelight.viewmodel.components.StatusShapeState.Near
        Computer.State.READY_TO_PAIR -> com.limelight.viewmodel.components.StatusShapeState.Near
        Computer.State.OFFLINE -> com.limelight.viewmodel.components.StatusShapeState.Far
        Computer.State.CONNECTING -> com.limelight.viewmodel.components.StatusShapeState.Orbit
        Computer.State.ERROR -> com.limelight.viewmodel.components.StatusShapeState.Far
    }

    return ComputerItemUiState(
        name = details.name ?: "NO_NAME",
        address = address,
        statusText = statusText,
        statusColor = statusColor,
        statusTextColor = statusTextColor,
        actionText = actionText,
        actionTextColor = actionTextColor,
        cardColor = cardColor,
        statusShapeState = statusShapeState
    )
}

val Computer.pairPinText: String
    @Composable
    get() = when (pairPin) {
        null -> "XXXX" // TODO: add animation
        else -> "$pairPin"
    }

val Computer.detailsList: List<Pair<String, String>>
    @Composable
    get() {
        val details = details
        return listOf(
            "Name" to (details.name ?: "NULL"),
            "UUID" to (details.uuid ?: "NULL"),
            "State" to details.state.toString(),
            "PairState" to (details.pairState?.toString() ?: "NULL"),
            "Pair PIN" to (pairPin ?: "NULL"),
            "Active Address" to (details.activeAddress?.toString() ?: "NULL"),
            "Local Address" to (details.localAddress?.toString() ?: "NULL"),
            "Remote Address" to (details.remoteAddress?.toString() ?: "NULL"),
            "Manual Address" to (details.manualAddress?.toString() ?: "NULL"),
            "IPv6 Address" to (details.ipv6Address?.toString() ?: "NULL"),
            "MAC Address" to (details.macAddress ?: "NULL"),
            "HTTPS Port" to details.httpsPort.toString(),
            "External Port" to details.externalPort.toString(),
            "Running Game ID" to details.runningGameId.toString(),
            "NVIDIA Server" to details.nvidiaServer.toString(),
        )
    }

val NvApp.detailsList: List<Pair<String, String>>
    @Composable
    get() = listOf(
        "App Name" to (appName ?: "NULL"),
        "App ID" to appId.toString(),
        "HDR Supported" to isHdrSupported.toString(),
    )

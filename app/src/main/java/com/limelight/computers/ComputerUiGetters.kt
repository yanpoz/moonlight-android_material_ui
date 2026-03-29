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
        ComputerDetails.State.UNKNOWN -> "Waiting response from PC"
        else -> {
            if (details.pairState != PairingManager.PairState.PAIRED) {
                "Pair to PC"
            } else {
                val runningApp = getRunningApp()
                if (runningApp != null) {
                    "Connect to running: ${runningApp.appName}"
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

    val statusText = if (details.state == ComputerDetails.State.UNKNOWN) {
        "Connecting..."
    } else if (pairStatusText != null) {
        "$networkStateText • $pairStatusText"
    } else {
        networkStateText
    }

    val statusColor = when (details.state) {
        ComputerDetails.State.ONLINE -> {
            if (details.pairState == PairingManager.PairState.PAIRED) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        }
        ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.secondary
        ComputerDetails.State.UNKNOWN -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val actionTextColor = when (details.state) {
        ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.UNKNOWN -> MaterialTheme.colorScheme.secondary
    }

    return ComputerItemUiState(
        uuid = details.uuid,
        name = details.name ?: "Unknown",
        address = address,
        statusText = statusText,
        statusColor = statusColor,
        actionText = actionText,
        actionTextColor = actionTextColor
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

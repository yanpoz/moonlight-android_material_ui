package com.limelight.computers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import com.limelight.viewmodel.components.ComputerItemUiState
import com.limelight.viewmodel.components.StatusIndicatorUiState
import com.limelight.viewmodel.components.StatusShapeState


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

    val actionTextColor = when (details.state) {
        ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.UNKNOWN -> MaterialTheme.colorScheme.secondary
    }

    val cardColor = when (state) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val statusShapeState = when (state) {
        Computer.State.STREAMING -> StatusShapeState.Orbit
        Computer.State.READY_TO_CONNECT -> StatusShapeState.Near
        Computer.State.READY_TO_PAIR -> StatusShapeState.Near
        Computer.State.OFFLINE -> StatusShapeState.Far
        Computer.State.CONNECTING -> StatusShapeState.Far
        Computer.State.ERROR -> StatusShapeState.Near
    }

    val statusIndicator = StatusIndicatorUiState(
        atmosphereShapeState = statusShapeState,
        planetShapeState = statusShapeState,
        color = when (state) {
            Computer.State.STREAMING -> MaterialTheme.colorScheme.tertiary
            Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.primary
            Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.tertiary
            Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary
            Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondary
            Computer.State.ERROR -> MaterialTheme.colorScheme.error
        },
        text = when (state) {
            Computer.State.STREAMING -> "Streaming"
            Computer.State.READY_TO_CONNECT -> "Ready to connect"
            Computer.State.READY_TO_PAIR -> "Ready to pair"
            Computer.State.OFFLINE -> "Offline"
            Computer.State.CONNECTING -> "Connecting"
            Computer.State.ERROR -> "Error"
        },
        textColor = when (state) {
            Computer.State.STREAMING -> MaterialTheme.colorScheme.onTertiary
            Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.primary
            Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.tertiary
            Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary
            Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondary
            Computer.State.ERROR -> MaterialTheme.colorScheme.error
        },
        offsetX = when (statusShapeState) {
            StatusShapeState.Far -> (-70).dp
            StatusShapeState.Near -> (-70).dp
            StatusShapeState.Orbit -> (-40).dp
        },
        offsetY = when (statusShapeState) {
            StatusShapeState.Far -> (-70).dp
            StatusShapeState.Near -> (-70).dp
            StatusShapeState.Orbit -> (-30).dp
        }
    )

    return ComputerItemUiState(
        name = details.name ?: "NO_NAME",
        address = address,
        actionText = actionText,
        actionTextColor = actionTextColor,
        cardColor = cardColor,
        statusIndicator = statusIndicator
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

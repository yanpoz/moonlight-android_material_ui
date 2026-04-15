package com.limelight.computers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.nvstream.http.NvApp
import com.limelight.viewmodel.components.ComputerItemUiState
import com.limelight.viewmodel.components.StatusIndicatorUiState
import com.limelight.viewmodel.components.StatusTextUiState
import com.limelight.viewmodel.components.StatusShapeState

@Composable
fun Computer.toUiState(): ComputerItemUiState {
    val computerState = this.state
    val runningApp = getRunningApp()
    
    // Resolve Display Address
    val address = details.activeAddress?.toString()
        ?: details.localAddress?.toString()
        ?: details.remoteAddress?.toString()
        ?: details.manualAddress?.toString()
        ?: "UNKNOWN"

    // Determine Action Button UI
    val actionText = when (computerState) {
        Computer.State.OFFLINE, Computer.State.CONNECTING -> stringResource(R.string.pcview_menu_send_wol)
        Computer.State.READY_TO_PAIR -> stringResource(R.string.pcview_menu_pair_pc)
        Computer.State.STREAMING -> runningApp?.let { "Connect to: ${it.appName}" } ?: "Connect to Desktop"
        Computer.State.READY_TO_CONNECT -> "Connect to Desktop"
        Computer.State.ERROR -> "Error"
    }

    val actionTextColor = if (computerState == Computer.State.CONNECTING) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    // Status Indicator Theme Mapping
    val statusShape = when (computerState) {
        Computer.State.STREAMING -> StatusShapeState.Orbit
        Computer.State.READY_TO_CONNECT, Computer.State.READY_TO_PAIR, Computer.State.ERROR -> StatusShapeState.Near
        Computer.State.OFFLINE, Computer.State.CONNECTING -> StatusShapeState.Far
    }

    val (statusColor, statusText) = when (computerState) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.secondary to "Streaming"
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.secondary to "Ready to connect"
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.secondary to "Ready to pair"
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary to "Offline"
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondary to "Connecting"
        Computer.State.ERROR -> MaterialTheme.colorScheme.error to "Error"
    }

    val statusTextColor = when (computerState) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.tertiary
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.tertiary
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.tertiary
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondary
        Computer.State.ERROR -> MaterialTheme.colorScheme.error
    }

    val (offsetX, offsetY) = when (statusShape) {
        StatusShapeState.Far -> (-70).dp to 10.dp
        StatusShapeState.Near -> (0).dp to 10.dp
        StatusShapeState.Orbit -> (-40).dp to 90.dp
    }

    val atmosphereSize = when (statusShape) {
        StatusShapeState.Far -> 120.dp
        StatusShapeState.Near -> 250.dp
        StatusShapeState.Orbit -> 450.dp
    }

    val planetSize = when (statusShape) {
        StatusShapeState.Far -> 60.dp
        StatusShapeState.Near -> 200.dp
        StatusShapeState.Orbit -> 350.dp
    }

    return ComputerItemUiState(
        name = details.name ?: "NO_NAME", //TODO: is it possible?
        address = address,
        actionText = actionText,
        actionTextColor = actionTextColor,
        cardColor = if (computerState == Computer.State.STREAMING) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        statusIndicator = StatusIndicatorUiState(
            atmosphereSize = atmosphereSize,
            planetSize = planetSize,
            planetColor = statusColor,
            offsetX = offsetX,
            offsetY = offsetY
        ),
        statusText = StatusTextUiState(
            text = statusText,
            textColor = statusTextColor
        )
    )
}

val Computer.pairPinText: String
    @Composable
    get() = pairPin ?: "XXXX"

val Computer.detailsList: List<Pair<String, String>>
    @Composable
    get() = listOf(
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

val NvApp.detailsList: List<Pair<String, String>>
    @Composable
    get() = listOf(
        "App Name" to (appName ?: "NULL"),
        "App ID" to appId.toString(),
        "HDR Supported" to isHdrSupported.toString(),
    )

package com.limelight.computers

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.nvstream.http.NvApp
import com.limelight.viewmodel.components.ActionLabelUiState
import com.limelight.viewmodel.components.ComputerItemUiState
import com.limelight.viewmodel.components.StatusIndicatorUiState
import com.limelight.viewmodel.components.StatusLabelUiState
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
    val (actionText, actionIcon) = when (computerState) {
        Computer.State.OFFLINE, Computer.State.CONNECTING -> "Send WOL" to Icons.Outlined.PowerSettingsNew
        Computer.State.READY_TO_PAIR -> stringResource(R.string.pcview_menu_pair_pc) to Icons.Outlined.Handshake
        Computer.State.STREAMING -> (runningApp?.let { "Resume: ${it.appName}" } ?: "Resume") to Icons.Outlined.PlayArrow
        Computer.State.READY_TO_CONNECT -> "Open Desktop" to Icons.AutoMirrored.Outlined.OpenInNew
        Computer.State.ERROR -> "Error" to Icons.Outlined.Warning
    }

    val targetActionTextColor = if (computerState == Computer.State.CONNECTING) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val actionTextColor by animateColorAsState(targetActionTextColor, label = "actionTextColor")

    // Status Indicator Theme Mapping
    val statusShape = when (computerState) {
        Computer.State.STREAMING -> StatusShapeState.Orbit
        Computer.State.READY_TO_CONNECT, Computer.State.READY_TO_PAIR, Computer.State.ERROR -> StatusShapeState.Near
        Computer.State.OFFLINE, Computer.State.CONNECTING -> StatusShapeState.Far
    }

    val (targetStatusColor, statusText) = when (computerState) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.tertiary to "Live"
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.tertiaryContainer to "Ready"
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.tertiaryContainer to "New"
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary to "Offline"
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondary to "Connecting"
        Computer.State.ERROR -> MaterialTheme.colorScheme.errorContainer to "Error"
    }
    val statusColor by animateColorAsState(targetStatusColor, label = "statusColor")

    val targetPlanetColor = when (computerState) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondaryContainer
        Computer.State.ERROR -> MaterialTheme.colorScheme.errorContainer
    }
    val planetColor by animateColorAsState(targetPlanetColor, label = "planetColor")

    val targetStatusTextColor = when (computerState) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.onTertiary
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.onTertiaryContainer
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.onTertiaryContainer
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.onSecondary
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.onSecondary
        Computer.State.ERROR -> MaterialTheme.colorScheme.onErrorContainer
    }
    val statusTextColor by animateColorAsState(targetStatusTextColor, label = "statusTextColor")

    val (targetOffsetX, targetOffsetY) = when (statusShape) {
        StatusShapeState.Far -> (-70).dp to 10.dp
        StatusShapeState.Near ->(-20).dp to 20.dp
        StatusShapeState.Orbit -> 0.dp to 120.dp
    }
    val offsetX by animateDpAsState(targetOffsetX, label = "offsetX")
    val offsetY by animateDpAsState(targetOffsetY, label = "offsetY")

    val targetAtmosphereSize = when (statusShape) {
        StatusShapeState.Far -> 120.dp
        StatusShapeState.Near -> 250.dp
        StatusShapeState.Orbit -> 450.dp
    }
    val atmosphereSize by animateDpAsState(targetAtmosphereSize, label = "atmosphereSize")

    val targetAtmosphereColor = when (computerState) {
        Computer.State.STREAMING -> MaterialTheme.colorScheme.secondary
        Computer.State.READY_TO_CONNECT -> MaterialTheme.colorScheme.secondary
        Computer.State.READY_TO_PAIR -> MaterialTheme.colorScheme.secondary
        Computer.State.OFFLINE -> MaterialTheme.colorScheme.secondary
        Computer.State.CONNECTING -> MaterialTheme.colorScheme.secondary
        Computer.State.ERROR -> MaterialTheme.colorScheme.error
    }
    val atmosphereColor by animateColorAsState(targetAtmosphereColor, label = "atmosphereColor")

    val targetPlanetSize = when (statusShape) {
        StatusShapeState.Far -> 60.dp
        StatusShapeState.Near -> 150.dp
        StatusShapeState.Orbit -> 400.dp
    }
    val planetSize by animateDpAsState(targetPlanetSize, label = "planetSize")

    return ComputerItemUiState(
        name = details.name ?: "NO_NAME", //TODO: is it possible?
        address = address,
        actionLabel = ActionLabelUiState(
            text = actionText,
            textColor = actionTextColor,
            icon = actionIcon
        ),
        statusIndicator = StatusIndicatorUiState(
            atmosphereSize = atmosphereSize,
            atmosphereColor = atmosphereColor,
            planetSize = planetSize,
            planetColor = planetColor,
            offsetX = offsetX,
            offsetY = offsetY
        ),
        statusLabel = StatusLabelUiState(
            text = statusText,
            textColor = statusTextColor,
            statusColor = statusColor
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

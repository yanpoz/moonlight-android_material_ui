package com.limelight.computers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.limelight.R
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager

@Composable
fun getComputerAddressText(computer: Computer): String {
    val address = computer.details.activeAddress?.toString()
        ?: computer.details.localAddress?.toString()
        ?: computer.details.remoteAddress?.toString()
        ?: computer.details.manualAddress?.toString()

    return address ?: "UNKNOWN"
}

@Composable
fun getComputerItemCardActionText(computer: Computer): String {
    return when (computer.details.state) {
        ComputerDetails.State.OFFLINE -> "Send Wake-On-LAN"
        ComputerDetails.State.UNKNOWN -> "Waiting response from PC"
        else -> {
            if (computer.details.pairState != PairingManager.PairState.PAIRED) {
                "Pair to PC"
            } else {
                val runningApp = computer.getRunningApp()
                if (runningApp != null) {
                    "Connect to running: ${runningApp.appName}"
                } else {
                    "Connect to Desktop"
                }
            }
        }
    }
}

@Composable
fun getComputerNetworkStateText(computer: Computer): String {
    return when (computer.details.state) {
        ComputerDetails.State.ONLINE -> "Online"
        ComputerDetails.State.OFFLINE -> "Offline"
        ComputerDetails.State.UNKNOWN -> "Unknown"
        else -> "NULL"
    }
}

@Composable
fun getComputerPairStatusText(computer: Computer): String {
    return when (computer.details.pairState) {
        PairingManager.PairState.PAIRED -> "Paired"
        PairingManager.PairState.NOT_PAIRED -> "Not paired"
        PairingManager.PairState.PIN_WRONG -> stringResource(R.string.pair_incorrect_pin)
        PairingManager.PairState.FAILED -> stringResource(R.string.pair_fail)
        PairingManager.PairState.ALREADY_IN_PROGRESS -> stringResource(R.string.pairing)
        null -> "NULL"
    }
}

@Composable
fun getComputerStatusText(computer: Computer): String {
    if (computer.details.state == ComputerDetails.State.UNKNOWN) {
        return "Connecting..."
    }
    return getComputerNetworkStateText(computer) + " • " + getComputerPairStatusText(computer)
}

@Composable
fun getComputerStatusColor(computer: Computer): Color {
    return when (computer.details.state) {
        ComputerDetails.State.ONLINE -> {
            if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        }
        ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.secondary
        ComputerDetails.State.UNKNOWN -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
fun getComputerActionTextColor(computer: Computer): Color {
    return when (computer.details.state) {
        ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.tertiary
        ComputerDetails.State.UNKNOWN -> MaterialTheme.colorScheme.secondary
    }
}

@Composable
fun getComputerPairPinText(computer: Computer): String {
    return when (computer.pairPin) {
        null -> "XXXX" // TODO: add animation
        else -> "${computer.pairPin}"
    }
}

@Composable
fun getComputerDetailsText(computer: Computer): List<Pair<String, String>> {
    val details = computer.details
    return listOf(
        "Name" to (details.name ?: "NULL"),
        "UUID" to (details.uuid ?: "NULL"),
        "State" to details.state.toString(),
        "PairState" to (details.pairState?.toString() ?: "NULL"),
        "Pair PIN" to (computer.pairPin ?: "NULL"),
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

@Composable
fun getAppDetails(app: NvApp): List<Pair<String, String>> {
    return listOf(
        "App Name" to (app.appName ?: "NULL"),
        "App ID" to app.appId.toString(),
        "HDR Supported" to app.isHdrSupported.toString(),
    )
}

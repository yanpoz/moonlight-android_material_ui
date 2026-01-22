package com.limelight.computers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.limelight.R
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager

@Composable
fun getComputerAddressText(computer: Computer): String {
    return computer.details.activeAddress?.address
        ?: computer.details.localAddress?.address
        ?: computer.details.remoteAddress?.address
        ?: computer.details.manualAddress?.address
        ?: stringResource(R.string.error_unknown_host)
}

@Composable
fun getComputerPairResultText(computer: Computer): String {
    return when (computer.pairResult) {
        null -> "NULL pair"
        PairingManager.PairState.ALREADY_IN_PROGRESS -> stringResource(R.string.pair_already_in_progress)
        PairingManager.PairState.PIN_WRONG -> stringResource(R.string.pair_incorrect_pin)
        PairingManager.PairState.FAILED -> {
            if (computer.details.runningGameId != 0) {
                stringResource(R.string.pair_pc_ingame)
            } else {
                stringResource(R.string.pair_fail)
            }
        }
        else -> "Pair Result: ${computer.pairResult}"
    }
}

@Composable
fun getComputerStatusColor(computer: Computer): Color {
    return when (computer.details.state) {
        ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.primary
        ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

fun getComputerPairPinText(computer: Computer): String {
    return when (computer.pairPin) {
        null -> "Generating PIN..." // TODO: add animation
        else -> "Pair PIN: ${computer.pairPin}"
    }
}

@Composable
fun getComputerPairStatusText(computer: Computer): String {
    return when (computer.details.pairState) {
        PairingManager.PairState.PAIRED -> "Pair status: Paired"
        PairingManager.PairState.NOT_PAIRED -> stringResource(R.string.scut_not_paired)
        PairingManager.PairState.PIN_WRONG -> stringResource(R.string.pair_incorrect_pin)
        PairingManager.PairState.FAILED -> stringResource(R.string.pair_fail)
        PairingManager.PairState.ALREADY_IN_PROGRESS -> stringResource(R.string.pairing)
        null -> stringResource(R.string.pair_fail) //TODO: loading
    }
}

@Composable
fun getComputerDetailsText(computer: Computer): List<Pair<String, String>> {
    val details = computer.details
    return listOfNotNull(
        "Name" to details.name,
        "UUID" to details.uuid,
        "State" to details.state.toString(),
        "Paired" to details.pairState.toString(),
        details.activeAddress?.let { "Active Address" to it.toString() },
        details.localAddress?.let { "Local Address" to it.toString() },
        details.remoteAddress?.let { "Remote Address" to it.toString() },
        details.manualAddress?.let { "Manual Address" to it.toString() },
        details.ipv6Address?.let { "IPv6 Address" to it.toString() },
        details.macAddress?.let { "MAC Address" to it },
        "HTTPS Port" to details.httpsPort.toString(),
        "External Port" to details.externalPort.toString(),
        "Running Game ID" to details.runningGameId.toString(),
        "NVIDIA Server" to details.nvidiaServer.toString(),
    )
}

fun isComputerPaired(computer: Computer): Boolean {
    return computer.pairResult == PairingManager.PairState.PAIRED
}
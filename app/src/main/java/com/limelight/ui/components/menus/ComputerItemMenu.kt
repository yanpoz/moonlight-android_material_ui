package com.limelight.ui.components.menus

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager

@Composable
fun ComputerItemMenu(
    computer: Computer,
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onSendWakeOnLan: () -> Unit,
    onQuitRunningApp: () -> Unit,
    onComputerDetailsClicked: () -> Unit,
    onDeleteComputer: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onTestNetwork: () -> Unit,
    onPairOrStart: () -> Unit,
    modifier: Modifier = Modifier,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true
) {
    DropdownMenu(
        modifier = modifier.widthIn(min = 220.dp),
        expanded = isExpanded,
        onDismissRequest = onDismissRequest
    ) {
        if (computer.details.state == ComputerDetails.State.OFFLINE ||
            computer.details.state == ComputerDetails.State.UNKNOWN
        ) {
            // Send Wake-On-LAN
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_send_wol)) },
                leadingIcon = { Icon(Icons.Outlined.PowerSettingsNew, null) },
                onClick = {
                    onDismissRequest()
                    onSendWakeOnLan()
                }
            )
            HorizontalDivider()
        } else if (computer.details.pairState != PairingManager.PairState.PAIRED) {
            // Pair PC
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_pair_pc)) },
                leadingIcon = { Icon(Icons.Outlined.Handshake, null) },
                onClick = {
                    onDismissRequest()
                    onPairOrStart()
                }
            )
            HorizontalDivider()
        } else {
            if (computer.details.runningGameId == 0) {
                // Start Session
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.applist_menu_resume)) },
                    leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                    onClick = {
                        onDismissRequest()
                        onPairOrStart()
                    }
                )
            } else {
                // Resume Session
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.applist_menu_resume)) },
                    leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                    onClick = {
                        onDismissRequest()
                        onPairOrStart()
                    }
                )
                // Quit Session
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.applist_menu_quit)) },
                    leadingIcon = { Icon(Icons.Outlined.Close, null) },
                    onClick = {
                        onDismissRequest()
                        onQuitRunningApp()
                    }
                )
            }
            HorizontalDivider()
        }
        // Move Up
        DropdownMenuItem(
            text = { Text(text = "Move Up") },
            leadingIcon = { Icon(Icons.Outlined.KeyboardArrowUp, null) },
            enabled = canMoveUp,
            onClick = {
                onDismissRequest()
                onMoveUp()
            }
        )
        // Move Down
        DropdownMenuItem(
            text = { Text(text = "Move Down") },
            leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
            enabled = canMoveDown,
            onClick = {
                onDismissRequest()
                onMoveDown()
            }
        )
        HorizontalDivider()
        // Test Network Connection
        DropdownMenuItem(
            text = { Text(stringResource(R.string.pcview_menu_test_network)) },
            // leadingIcon = { Icon(Icons.Outlined.Speed, null) },
            leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
            onClick = {
                onDismissRequest()
                onTestNetwork()
            }
        )
        // View Details
        DropdownMenuItem(
            text = { Text(stringResource(R.string.pcview_menu_details)) },
            // leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, null) },
            leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
            onClick = {
                onDismissRequest()
                onComputerDetailsClicked()
            }
        )
        // Create shortcut
        DropdownMenuItem(
            text = { Text(stringResource(R.string.applist_menu_scut)) },
            // leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
            leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
            onClick = { onDismissRequest() }
        )
        // Delete PC
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.pcview_menu_delete_pc),
                    color = MaterialTheme.colorScheme.error
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null
                )
            },
            onClick = {
                onDismissRequest()
                onDeleteComputer()
            }
        )
    }
}

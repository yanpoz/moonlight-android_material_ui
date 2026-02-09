package com.limelight.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.computers.getComputerAddressText
import com.limelight.computers.getComputerNetworkStateText
import com.limelight.computers.getComputerPairStatusText
import com.limelight.computers.getComputerStatusColor
import com.limelight.computers.getRunningGameName
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComputerItem(
    computer: Computer,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onSendWakeOnLan: () -> Unit,
    onQuitRunningApp: () -> Unit,
    onComputerDetailsClicked: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(16f / 9f) // Horizontal card (9:16 height:width)
            .clip(CardDefaults.shape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .onKeyEvent {
                if (it.key == Key.DirectionCenter) {
                    onLongClick()
                    return@onKeyEvent true
                }
                false
            }
            .focusable()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = computer.details.name,
                    style = MaterialTheme.typography.titleLarge
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(getComputerStatusColor(computer), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = getComputerAddressText(computer),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = getComputerNetworkStateText(computer),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = getComputerPairStatusText(computer),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = getRunningGameName(computer),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu( // TODO add menu caption
            modifier = Modifier.widthIn(min = 220.dp),
            expanded = isMenuExpanded,
            onDismissRequest = onDismissMenu
        ) {
            if (computer.details.state == ComputerDetails.State.OFFLINE ||
                computer.details.state == ComputerDetails.State.UNKNOWN
            ) {
                // Send Wake-On-LAN
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pcview_menu_send_wol)) },
                    leadingIcon = { Icon(Icons.Outlined.PowerSettingsNew, null) },
                    onClick = {
                        onDismissMenu()
                        onSendWakeOnLan()
                    }
                )
            } else if (computer.details.pairState != PairingManager.PairState.PAIRED) {
                // Pair PC
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pcview_menu_pair_pc)) },
                    leadingIcon = { Icon(Icons.Outlined.Handshake, null) },
                    onClick = {
                        onDismissMenu()
                        onClick()
                    }
                )
                HorizontalDivider() // TODO: replace with gap Material expressive
            } else {
                if (computer.details.runningGameId == 0) {
                    // Start Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_resume)) },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = {
                            onDismissMenu()
                            onClick()
                        }
                    )
                } else {
                    // Resume Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_resume)) },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = {
                            onDismissMenu()
                            onClick()
                        }
                    )
                    // Quit Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit)) },
                        leadingIcon = { Icon(Icons.Outlined.Close, null) },
                        onClick = {
                            onDismissMenu()
                            onQuitRunningApp()
                        }
                    )
                }
                HorizontalDivider() // TODO: replace with gap Material expressive
            }
            // Move Up TODO: should not be available when on top
            DropdownMenuItem(
                text = { Text(text = "Move Up") },
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowUp, null) },
                onClick = { onDismissMenu() /*TODO*/ }
            )
            // Move Down TODO: should not be available when on bottom
            DropdownMenuItem(
                text = { Text(text = "Move Down") },
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                onClick = { onDismissMenu() /*TODO*/ }
            )
            HorizontalDivider() // TODO: replace with gap Material expressive
            // Test Network Connection
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_test_network)) },
                // leadingIcon = { Icon(Icons.Outlined.Speed, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = { onDismissMenu() /*TODO*/ }
            )
            // View Details
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_details)) },
                // leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = {
                    onDismissMenu()
                    onComputerDetailsClicked()
                }
            )
            // Create shortcut
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_scut)) },
                // leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = { onDismissMenu() /*TODO*/ }
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
                onClick = { onDismissMenu() /*TODO*/ }
            )
        }
    }
}

@Preview
@Composable
fun ComputerItemPreview() {
    val computer = Computer(
        details = ComputerDetails().apply {
            name = "My Gaming PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )
    ComputerItem(
        computer = computer,
        isMenuExpanded = false,
        onDismissMenu = { },
        onSendWakeOnLan = { },
        onQuitRunningApp = { },
        onComputerDetailsClicked = { },
        onClick = { },
        onLongClick = { },
    )
}

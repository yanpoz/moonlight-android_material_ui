package com.limelight.ui.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.computers.getComputerPairStatusText
import com.limelight.computers.getComputerStatusColor
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager
import com.limelight.viewmodel.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComputerItem(
    viewModel: MainViewModel, computer: Computer, context: Context,
    onClick: () -> Unit, onLongClick: () -> Unit, modifier: Modifier = Modifier.Companion
) {
    Card(
        modifier = modifier
            .aspectRatio(16f / 9f) // Horizontal card (9:16 height:width)
            .clip(CardDefaults.shape)
            .combinedClickable(onClick=onClick, onLongClick=onLongClick)
    ) {
        Column(modifier = Modifier.Companion.padding(16.dp)) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = computer.details.name,
                    style = MaterialTheme.typography.titleLarge
                )

                // Status indicator
                Box(
                    modifier = Modifier.Companion
                        .size(12.dp)
                        .background(getComputerStatusColor(computer), CircleShape)
                )
            }

            Spacer(modifier = Modifier.Companion.height(4.dp))

            Text(
                text = getComputerPairStatusText(computer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            modifier = Modifier.Companion.widthIn(min = 220.dp),
            // TODO add caption
            expanded = viewModel.computerMenu.computerUuid == computer.details.uuid,
            onDismissRequest = { viewModel.dismissComputerMenu() }
        ) {
            if (computer.details.state == ComputerDetails.State.OFFLINE ||
                computer.details.state == ComputerDetails.State.UNKNOWN
            ) {
                // Send Wake-On-LAN
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pcview_menu_send_wol)) },
                    leadingIcon = { Icon(Icons.Outlined.PowerSettingsNew, null) },
                    onClick = {
                        viewModel.dismissComputerMenu()
                        viewModel.onSendWakeOnLan(context, computer.details.uuid)
                    }
                )
            } else if (computer.details.pairState != PairingManager.PairState.PAIRED) {
                // Pair PC
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pcview_menu_pair_pc)) },
                    leadingIcon = { Icon(Icons.Outlined.Handshake, null) },
                    onClick = {
                        viewModel.dismissComputerMenu()
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
                            viewModel.dismissComputerMenu()
                            onClick()
                        }
                    )
                } else {
                    // Resume Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_resume)) },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = {
                            viewModel.dismissComputerMenu()
                            onClick()
                        }
                    )
                    // Quit Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit)) },
                        leadingIcon = { Icon(Icons.Outlined.Close, null) },
                        onClick = {
                            viewModel.dismissComputerMenu()
                            viewModel.onQuitRunningApp(context, computer)
                        }
                    )
                }
                HorizontalDivider() // TODO: replace with gap Material expressive
            }
            // Move Up TODO: should not be available when on top
            DropdownMenuItem(
                text = { Text(text = "Move Up") },
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowUp, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            // Move Down TODO: should not be available when on bottom
            DropdownMenuItem(
                text = { Text(text = "Move Down") },
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            HorizontalDivider() // TODO: replace with gap Material expressive
            // Test Network Connection
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_test_network)) },
                // leadingIcon = { Icon(Icons.Outlined.Speed, null) },
                leadingIcon = { Spacer(modifier = Modifier.Companion.size(24.dp)) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            // View Details
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_details)) },
                // leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, null) },
                leadingIcon = { Spacer(modifier = Modifier.Companion.size(24.dp)) },
                onClick = {
                    viewModel.dismissComputerMenu()
                    viewModel.onComputerDetailsClicked(computer)
                }
            )
            // Create shortcut
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_scut)) },
                // leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
                leadingIcon = { Spacer(modifier = Modifier.Companion.size(24.dp)) },
                onClick = { viewModel.dismissAppMenu() /*TODO*/ }
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
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
        }
    }
}

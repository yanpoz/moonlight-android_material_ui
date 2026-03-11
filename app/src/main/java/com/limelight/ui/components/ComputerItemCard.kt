package com.limelight.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.computers.getComputerAddressText
import com.limelight.computers.getComputerStatusColor
import com.limelight.computers.getComputerStatusText
import com.limelight.computers.getComputerItemCardActionText
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager
import com.limelight.ui.theme.LocalIsDarkTheme
import com.limelight.ui.theme.MoonlightandroidTheme
import com.limelight.ui.theme.VerySunnyShape

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComputerItemCard(
    computer: Computer,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onSendWakeOnLan: () -> Unit,
    onQuitRunningApp: () -> Unit,
    onComputerDetailsClicked: () -> Unit,
    onDeleteComputer: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true
) {
    val isDark = LocalIsDarkTheme.current

    ItemCard(
        onClick = onClick,
        onLongClick = onLongClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = modifier
            .aspectRatio(16f / 9f) // Horizontal card (9:16 height:width)
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
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(VerySunnyShape)
                        .background(getComputerStatusColor(computer))
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = getComputerAddressText(computer),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    color =
                        if (isDark)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primaryFixed,
                ),
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(20.dp),
                        color =
                            if (isDark)
                                MaterialTheme.colorScheme.onPrimaryFixed
                            else
                                MaterialTheme.colorScheme.primary,
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getComputerStatusText(computer),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = getComputerItemCardActionText(computer),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        // TODO: replace with expressive menu, implement missing items
        DropdownMenu(
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
                HorizontalDivider()
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
                HorizontalDivider()
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
                HorizontalDivider()
            }
            // Move Up
            DropdownMenuItem(
                text = { Text(text = "Move Up") },
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowUp, null) },
                enabled = canMoveUp,
                onClick = {
                    onDismissMenu()
                    onMoveUp()
                }
            )
            // Move Down
            DropdownMenuItem(
                text = { Text(text = "Move Down") },
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                enabled = canMoveDown,
                onClick = {
                    onDismissMenu()
                    onMoveDown()
                }
            )
            HorizontalDivider()
            // Test Network Connection
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_test_network)) },
                // leadingIcon = { Icon(Icons.Outlined.Speed, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = { onDismissMenu() }
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
                onClick = { onDismissMenu() }
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
                    onDismissMenu()
                    onDeleteComputer()
                }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun ComputerItemCardGridPreview() {
    val onlinePairedComputer = Computer(
        details = ComputerDetails().apply {
            name = "Gaming PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )
    val onlineUnpairedComputer = Computer(
        details = ComputerDetails().apply {
            name = "Gaming PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.ONLINE
            pairState = PairingManager.PairState.NOT_PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )
    val offlineComputer = Computer(
        details = ComputerDetails().apply {
            name = "Offline PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.OFFLINE
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )
    val unknownComputer = Computer(
        details = ComputerDetails().apply {
            name = "Unknown PC"
            activeAddress = ComputerDetails.AddressTuple("192.168.1.1", 1234)
            state = ComputerDetails.State.UNKNOWN
            pairState = PairingManager.PairState.PAIRED
            runningGameId = 0
        },
        apps = emptyList()
    )

    val computerStates = listOf(
        "OnlinePaired" to onlinePairedComputer,
        "OnlineUnpaired" to onlineUnpairedComputer,
        "Offline" to offlineComputer,
        "Unknown" to unknownComputer
    )

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        computerStates.forEach { (label, computer) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Light version
                MoonlightandroidTheme(darkTheme = false) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        ComputerItemCard(
                            computer = computer,
                            isMenuExpanded = false,
                            onDismissMenu = { },
                            onSendWakeOnLan = { },
                            onQuitRunningApp = { },
                            onComputerDetailsClicked = { },
                            onDeleteComputer = { },
                            onMoveUp = { },
                            onMoveDown = { },
                            onClick = { },
                            onLongClick = { }
                        )
                    }
                }

                // Dark version
                MoonlightandroidTheme(darkTheme = true) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        ComputerItemCard(
                            computer = computer,
                            isMenuExpanded = false,
                            onDismissMenu = { },
                            onSendWakeOnLan = { },
                            onQuitRunningApp = { },
                            onComputerDetailsClicked = { },
                            onDeleteComputer = { },
                            onMoveUp = { },
                            onMoveDown = { },
                            onClick = { },
                            onLongClick = { }
                        )
                    }
                }
            }
        }
    }
}

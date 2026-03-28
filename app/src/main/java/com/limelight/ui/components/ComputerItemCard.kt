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
import androidx.compose.foundation.layout.offset
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
import com.limelight.computers.actionTextColor
import com.limelight.computers.addressText
import com.limelight.computers.itemCardActionText
import com.limelight.computers.statusColor
import com.limelight.computers.statusText
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager
import com.limelight.ui.theme.LocalIsDarkTheme
import com.limelight.ui.theme.MoonlightAndroidTheme
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
    onTestNetwork: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true
) {
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
                Title(computer)
                Indicator(computer)
            }

            Spacer(modifier = Modifier.height(4.dp))

            ComputerAddressBadge(computer)

            Spacer(modifier = Modifier.height(4.dp))

            ComputerStatusBadge(computer)

            Spacer(modifier = Modifier.weight(1f))

            ComputerActionLabel(computer)
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
                onClick = {
                    onDismissMenu()
                    onTestNetwork()
                }
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

@Composable
private fun Title(computer: Computer, modifier: Modifier = Modifier) {
    Text(
        text = computer.details.name,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Default,
        modifier = modifier
    )
}

@Composable
private fun Indicator(computer: Computer, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(VerySunnyShape)
            .background(computer.statusColor)
    )
}

@Composable
private fun ComputerAddressBadge(computer: Computer, modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    Text(
        text = computer.addressText,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            color = if (isDark)
                MaterialTheme.colorScheme.secondary
            else
                MaterialTheme.colorScheme.secondaryFixed,
        ),
        modifier = modifier
            .offset(x = (-6).dp)
            .background(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark)
                    MaterialTheme.colorScheme.onSecondaryFixed
                else
                    MaterialTheme.colorScheme.secondary,
            )
            .border(
                width = 1.dp,
                color = if (isDark)
                    MaterialTheme.colorScheme.onSecondaryFixed
                else
                    MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun ComputerStatusBadge(computer: Computer, modifier: Modifier = Modifier) {
    Text(
        text = computer.statusText,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.secondary
        ),
        modifier = modifier
            .offset(x = (-6).dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun ComputerActionLabel(computer: Computer, modifier: Modifier = Modifier) {
    Text(
        text = computer.itemCardActionText,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = computer.actionTextColor
        ),
        modifier = modifier
    )
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
                MoonlightAndroidTheme("light") {
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
                            onTestNetwork = { },
                            onClick = { },
                            onLongClick = { }
                        )
                    }
                }

                // Dark version
                MoonlightAndroidTheme("dark") {
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
                            onTestNetwork = { },
                            onClick = { },
                            onLongClick = { }
                        )
                    }
                }
            }
        }
    }
}

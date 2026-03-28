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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.computers.Computer
import com.limelight.computers.actionTextColor
import com.limelight.computers.addressText
import com.limelight.computers.itemCardActionText
import com.limelight.computers.statusColor
import com.limelight.computers.statusText
import com.limelight.ui.theme.LocalIsDarkTheme
import com.limelight.ui.theme.MoonlightAndroidTheme
import com.limelight.ui.theme.VerySunnyShape
import com.limelight.ui.utils.SampleComputers

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

        ComputerItemMenu(
            computer = computer,
            isExpanded = isMenuExpanded,
            onDismissRequest = onDismissMenu,
            onSendWakeOnLan = onSendWakeOnLan,
            onQuitRunningApp = onQuitRunningApp,
            onComputerDetailsClicked = onComputerDetailsClicked,
            onDeleteComputer = onDeleteComputer,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onTestNetwork = onTestNetwork,
            onPairOrStart = onClick,
            canMoveUp = canMoveUp,
            canMoveDown = canMoveDown
        )
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
    val computerStates = listOf(
        "OnlinePaired" to SampleComputers.OnlinePairedComputer,
        "OnlineUnpaired" to SampleComputers.OnlineUnpairedComputer,
        "Offline" to SampleComputers.OfflineComputer,
        "Unknown" to SampleComputers.UnknownComputer
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

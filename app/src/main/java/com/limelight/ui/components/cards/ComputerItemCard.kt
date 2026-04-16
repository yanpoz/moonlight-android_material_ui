package com.limelight.ui.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.limelight.computers.toUiState
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.ui.components.menus.ComputerItemMenu
import com.limelight.ui.theme.LocalIsDarkTheme
import com.limelight.ui.theme.MoonlightAndroidTheme
import com.limelight.ui.theme.VerySunnyShape
import com.limelight.ui.utils.SampleComputers
import com.limelight.viewmodel.components.ActionLabelUiState
import com.limelight.viewmodel.components.ComputerItemUiState
import com.limelight.viewmodel.components.StatusIndicatorUiState
import com.limelight.viewmodel.components.StatusLabelUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComputerItemCard(
    uiState: ComputerItemUiState,
    details: ComputerDetails,
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
        Box(modifier = Modifier.fillMaxSize()) {
            StatusIndicator(
                uiState = uiState.statusIndicator,
                modifier = Modifier.matchParentSize()
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Title(uiState.name)
                AddressBadge(uiState.address)
                StatusLabel(
                    uiState = uiState.statusLabel,
                )
                // TODO move outside
                ActionLabel(
                    uiState = uiState.actionLabel,
                )
            }

            ComputerItemMenu(
                details = details,
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
}

@Composable
private fun Title(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        style = MaterialTheme.typography.headlineLarge.copy(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Default,
        modifier = modifier
            .offset(x = (-12).dp, y = 0.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                shape = RoundedCornerShape(100.dp)
            )
            .padding(horizontal = 12.dp, vertical = 2.dp)
    )
}

@Composable
private fun AddressBadge(address: String, modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    Text(
        text = address,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace,
            color = if (!isDark)
                MaterialTheme.colorScheme.primaryFixed
            else
                MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier
            .offset(x = (-4).dp)
            .background(
                shape = RoundedCornerShape(4.dp),
                color = if (!isDark)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onPrimaryFixed,
            )
            .padding(horizontal = 8.dp, vertical = 1.dp)
    )
}


@Composable
private fun AtmosphereStatusIndicatorShape(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(VerySunnyShape)
            .background(
                color = color
            )
    ) {}
}

@Composable
private fun PlanetStatusIndicatorShape(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(VerySunnyShape)
            .background(color),
    ) {}
}

@Composable
private fun StatusLabel(uiState: StatusLabelUiState, modifier: Modifier = Modifier) {
    Text(
        text = uiState.text,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = uiState.textColor,
            fontWeight = FontWeight.SemiBold
        ),
        textAlign = TextAlign.End,
        modifier = modifier
    )
}

@Composable
private fun StatusIndicator(uiState: StatusIndicatorUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .size(0.dp)
                .offset(x = uiState.offsetX, y = uiState.offsetY)
                .wrapContentSize(align = Alignment.Center, unbounded = true),
            contentAlignment = Alignment.Center
        ) {
            AtmosphereStatusIndicatorShape(
                color = uiState.atmosphereColor,
                size = uiState.atmosphereSize,
            )
            PlanetStatusIndicatorShape(
                color = uiState.planetColor,
                size = uiState.planetSize
            )
        }
    }
}

@Composable
private fun ActionLabel(uiState: ActionLabelUiState, modifier: Modifier = Modifier) {
    Text(
        text = uiState.text,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = uiState.textColor
        ),
        textAlign = TextAlign.End,
        modifier = modifier
    )
}


//@Preview
@Composable
fun ComputerItemCardPreview() {
    val computer = SampleComputers.PairingFailedComputer
    MoonlightAndroidTheme(dynamicColor = false) {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)) {
            ComputerItemCard(
                uiState = computer.toUiState(),
                details = computer.details,
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

@Preview(showBackground = true, widthDp = 700, heightDp = 1200)
@Composable
fun ComputerItemCardGridPreview() {
    val computerStates = listOf(
        "Running game" to SampleComputers.RunningGameComputer,
        "Online Paired" to SampleComputers.OnlinePairedComputer,
        "Online Unpaired" to SampleComputers.OnlineUnpairedComputer,
        "Offline" to SampleComputers.OfflineComputer,
        "Connecting" to SampleComputers.UnknownComputer,
        "Pairing Failed" to SampleComputers.PairingFailedComputer
    )
    val themes = listOf("light", "dark")

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        themes.forEach { theme ->
            MoonlightAndroidTheme(theme, dynamicColor = false) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    computerStates.forEach { (_, computer) ->
                        ComputerItemCard(
                            uiState = computer.toUiState(),
                            details = computer.details,
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

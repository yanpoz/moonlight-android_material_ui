package com.limelight.ui.components.cards

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.viewmodel.components.StatusLabelUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    statusLabel: StatusLabelUiState? = null,
    overlayContent: @Composable BoxScope.(isFocused: Boolean) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val cardShape = RoundedCornerShape(22.dp)

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1.0f,
        label = "scale"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isFocused) 16.dp else 2.dp,
        label = "elevation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = if (isFocused) 6.dp else 0.dp,
                    shape = cardShape,
                    spotColor = if (isFocused) MaterialTheme.colorScheme.primary else Color.Black,
                    ambientColor = if (isFocused) MaterialTheme.colorScheme.primary else Color.Black
                )
                .border(
                    border = if (isFocused) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(0.dp, Color.Transparent)
                    },
                    shape = cardShape
                )
                .clip(cardShape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                    interactionSource = interactionSource,
                    indication = null
                )
                .focusable(interactionSource = interactionSource),
            shape = cardShape,
            colors = colors,
            elevation = if (isFocused) CardDefaults.cardElevation(defaultElevation = animatedElevation) else elevation,
            content = content
        )

        statusLabel?.let { label ->
            val labelOffsetX by animateDpAsState(targetValue = if (isFocused) (-8).dp else 8.dp, label = "labelOffsetX")
            val labelOffsetY by animateDpAsState(targetValue = if (isFocused) 8.dp else (-8).dp, label = "labelOffsetY")
            StatusLabel(
                uiState = label,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = labelOffsetX, y = labelOffsetY)
            )
        }

        overlayContent(isFocused)
    }
}

@Composable
private fun StatusLabel(uiState: StatusLabelUiState, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(28.dp)
    Text(
        text = uiState.text,
        style = MaterialTheme.typography.labelLarge.copy(
            color = uiState.textColor,
            fontWeight = FontWeight.ExtraBold
        ),
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                spotColor = uiState.statusColor,
                ambientColor = uiState.statusColor
            )
            .background(
                color = uiState.statusColor,
                shape = shape
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ItemCardFocusPreview() {
    val statusLabel = StatusLabelUiState(
        text = "Online",
        textColor = Color.White,
        statusColor = Color(0xFF4CAF50)
    )
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ItemCard(
            onClick = {},
            onLongClick = {},
            modifier = Modifier.size(150.dp, 100.dp),
            statusLabel = statusLabel
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Unfocused")
            }
        }

        // Focused state simulation
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = 1.1f
                    scaleY = 1.1f
                }
        ) {
            Card(
                modifier = Modifier
                    .size(150.dp, 100.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CardDefaults.shape,
                        spotColor = MaterialTheme.colorScheme.primary,
                        ambientColor = MaterialTheme.colorScheme.primary
                    )
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        shape = CardDefaults.shape
                    )
                    .clip(CardDefaults.shape),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Focused")
                }
            }

            StatusLabel(
                uiState = statusLabel,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
            )
        }
    }
}

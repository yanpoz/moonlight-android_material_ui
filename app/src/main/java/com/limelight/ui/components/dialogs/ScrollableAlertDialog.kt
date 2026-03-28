package com.limelight.ui.components.dialogs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (scrollState.canScrollBackward) {
                    HorizontalDivider(
                        modifier = Modifier.bleeding(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                Box(modifier = Modifier.weight(1f, fill = false)) {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState),
                        content = content
                    )
                }
                if (scrollState.canScrollForward) {
                    HorizontalDivider(
                        modifier = Modifier.bleeding(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    )
}

// TODO: check if needed with Material Express
private fun Modifier.bleeding() = this.layout { measurable, constraints ->
    val horizontalPadding = 24.dp.roundToPx()
    val placeable = measurable.measure(constraints.copy(
        maxWidth = constraints.maxWidth + horizontalPadding * 2,
        minWidth = constraints.maxWidth + horizontalPadding * 2
    ))
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(-horizontalPadding, 0)
    }
}

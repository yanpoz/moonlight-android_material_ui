package com.limelight.ui.components.dialogs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isFullScreen = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT ||
            adaptiveInfo.windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT

    if (isFullScreen) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                title?.let {
                                    ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
                                        it()
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = onDismissRequest) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                                }
                            },
                            actions = {
                                confirmButton()
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        if (scrollState.canScrollBackward) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                                    .padding(horizontal = 24.dp, vertical = 8.dp),
                                content = content
                            )
                        }
                        if (scrollState.canScrollForward) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                        if (dismissButton != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                dismissButton()
                            }
                        }
                    }
                }
            }
        }
    } else {
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
}

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

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun AdaptiveDialogFullScreenPreview() {
    AdaptiveDialog(
        onDismissRequest = {},
        confirmButton = { androidx.compose.material3.TextButton(onClick = {}) { Text("Save") } },
        dismissButton = { androidx.compose.material3.TextButton(onClick = {}) { Text("Cancel") } },
        title = { Text("Settings") },
        content = {
            repeat(20) {
                Text("Item $it", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    )
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
fun AdaptiveDialogCenteredPreview() {
    AdaptiveDialog(
        onDismissRequest = {},
        confirmButton = { androidx.compose.material3.TextButton(onClick = {}) { Text("Save") } },
        dismissButton = { androidx.compose.material3.TextButton(onClick = {}) { Text("Cancel") } },
        title = { Text("Settings") },
        content = {
            repeat(5) {
                Text("Item $it", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    )
}

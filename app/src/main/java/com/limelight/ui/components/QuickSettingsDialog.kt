package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.ui.theme.MoonlightandroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSettingsDialog(
    fps: String,
    onFpsChanged: (String) -> Unit,
    resolution: String,
    onResolutionChanged: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val fpsValues = stringArrayResource(R.array.fps_values)
    val resolutionValues = stringArrayResource(R.array.resolution_values)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Settings") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.title_resolution_list),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    resolutionValues.forEachIndexed { index, value ->
                        val label = value.split("x").getOrNull(1) ?: value
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index, count = resolutionValues.size
                            ),
                            onClick = { onResolutionChanged(value) },
                            selected = resolution == value
                        ) {
                            Text(label)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.title_fps_list),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    fpsValues.forEachIndexed { index, value ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index, count = fpsValues.size
                            ),
                            onClick = { onFpsChanged(value) },
                            selected = fps == value
                        ) {
                            Text(value)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}

@Preview
@Composable
fun QuickSettingsDialogPreview() {
    MoonlightandroidTheme {
        QuickSettingsDialog(
            fps = "60",
            onFpsChanged = {},
            resolution = "1280x720",
            onResolutionChanged = {},
            onDismiss = {}
        )
    }
}

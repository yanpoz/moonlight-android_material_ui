package com.limelight.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.ui.theme.MoonlightAndroidTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickSettingsDialog(
    fps: String,
    onFpsChanged: (String) -> Unit,
    resolution: String,
    onResolutionChanged: (String) -> Unit,
    bitrate: Float,
    onBitrateChanged: (Float) -> Unit,
    touchscreenTrackpad: Boolean,
    onTouchscreenTrackpadChanged: (Boolean) -> Unit,
    onscreenController: Boolean,
    onOnscreenControllerChanged: (Boolean) -> Unit,
    hostAudio: Boolean,
    onHostAudioChanged: (Boolean) -> Unit,
    mouseEmulation: Boolean,
    onMouseEmulationChanged: (Boolean) -> Unit,
    vibrateOsc: Boolean,
    onVibrateOscChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val fpsValues = stringArrayResource(R.array.fps_values)
    val resolutionValues = stringArrayResource(R.array.resolution_values)
    var sliderValue by remember { mutableFloatStateOf(bitrate) }
    var isSliderFocused by remember { mutableStateOf(false) }
    val inputModeManager = LocalInputModeManager.current

    ScrollableAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Settings") },
        content = {
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.title_seekbar_bitrate),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "%.1f".format(sliderValue),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(8.dp))
                val showFocusedBorder = isSliderFocused && inputModeManager.inputMode == InputMode.Keyboard
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onBitrateChanged(it)
                    },
                    valueRange = 0.5f..150f,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isSliderFocused = it.isFocused }
                        .focusable()
                        .border(
                            width = if (showFocusedBorder) 2.dp else 0.dp,
                            color = if (showFocusedBorder) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp)
                        .onKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown) {
                                val range = 150f - 0.5f
                                val step = (range / 50f).coerceAtLeast(0.1f)
                                when (event.key) {
                                    Key.DirectionLeft -> {
                                        sliderValue = (sliderValue - step).coerceIn(0.5f, 150f)
                                        onBitrateChanged(sliderValue)
                                        true
                                    }
                                    Key.DirectionRight -> {
                                        sliderValue = (sliderValue + step).coerceIn(0.5f, 150f)
                                        onBitrateChanged(sliderValue)
                                        true
                                    }
                                    else -> false
                                }
                            } else {
                                false
                            }
                        }
                )
                Text(
                    text = stringResource(R.string.suffix_seekbar_bitrate_mbps),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = touchscreenTrackpad,
                    onClick = { onTouchscreenTrackpadChanged(!touchscreenTrackpad) },
                    label = { Text(stringResource(R.string.title_checkbox_touchscreen_trackpad)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (touchscreenTrackpad) Icons.Default.Done else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                )
                FilterChip(
                    selected = onscreenController,
                    onClick = { onOnscreenControllerChanged(!onscreenController) },
                    label = { Text(stringResource(R.string.title_checkbox_show_onscreen_controls)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (onscreenController) Icons.Default.Done else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                )
                FilterChip(
                    selected = hostAudio,
                    onClick = { onHostAudioChanged(!hostAudio) },
                    label = { Text(stringResource(R.string.title_checkbox_host_audio)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (hostAudio) Icons.Default.Done else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                )
                FilterChip(
                    selected = mouseEmulation,
                    onClick = { onMouseEmulationChanged(!mouseEmulation) },
                    label = { Text(stringResource(R.string.title_checkbox_mouse_emulation)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (mouseEmulation) Icons.Default.Done else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                )
                FilterChip(
                    selected = vibrateOsc,
                    onClick = { onVibrateOscChanged(!vibrateOsc) },
                    label = { Text(stringResource(R.string.title_checkbox_vibrate_osc)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (vibrateOsc) Icons.Default.Done else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                )
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
    MoonlightAndroidTheme {
        QuickSettingsDialog(
            fps = "60",
            onFpsChanged = {},
            resolution = "1280x720",
            onResolutionChanged = {},
            bitrate = 50f,
            onBitrateChanged = {},
            touchscreenTrackpad = true,
            onTouchscreenTrackpadChanged = {},
            onscreenController = false,
            onOnscreenControllerChanged = {},
            hostAudio = false,
            onHostAudioChanged = {},
            mouseEmulation = true,
            onMouseEmulationChanged = {},
            vibrateOsc = true,
            onVibrateOscChanged = {},
            onDismiss = {}
        )
    }
}

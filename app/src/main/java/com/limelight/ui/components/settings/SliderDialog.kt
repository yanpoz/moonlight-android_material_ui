package com.limelight.ui.components.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.repository.SettingItem
import kotlinx.coroutines.delay

@Composable
fun SliderDialog(
    item: SettingItem.Slider,
    onDismiss: () -> Unit,
    onValueChange: (Float) -> Unit
) {
    val isMbps = item.unit == R.string.suffix_seekbar_bitrate_mbps
    var sliderValue by remember { mutableFloatStateOf(item.value) }
    var textValue by remember {
        mutableStateOf(
            if (isMbps) "%.1f".format(item.value) else item.value.toInt().toString()
        )
    }
    val focusRequester = remember { FocusRequester() }
    var isSliderFocused by remember { mutableStateOf(false) }
    val inputModeManager = LocalInputModeManager.current

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(item.title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = stringResource(item.summary),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            textValue = newValue
                            newValue.toFloatOrNull()?.let { parsed ->
                                val clamped = parsed.coerceIn(item.min, item.max)
                                sliderValue = clamped
                            }
                        },
                        modifier = Modifier.width(120.dp),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    item.unit?.let {
                        Text(
                            text = stringResource(it),
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.min.toInt().toString(),
                        modifier = Modifier.padding(end = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    val showFocusedBorder = isSliderFocused && inputModeManager.inputMode == InputMode.Keyboard
                    Slider(
                        value = sliderValue,
                        onValueChange = {
                            sliderValue = it
                            textValue = if (isMbps) "%.1f".format(it) else it.toInt().toString()
                        },
                        valueRange = item.min..item.max,
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { isSliderFocused = it.isFocused }
                            .focusRequester(focusRequester)
                            .focusable()
                            // TODO: should be focused style/state should look like in docs
                            // https://m3.material.io/components/sliders/specs
                            .border(
                                width = if (showFocusedBorder) 2.dp else 0.dp,
                                color = if (showFocusedBorder) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp)
                            .onKeyEvent { event ->
                                if (event.type == KeyEventType.KeyDown) {
                                    val range = item.max - item.min
                                    val step = (range / 50f).coerceAtLeast(0.1f)
                                    when (event.key) {
                                        Key.DirectionLeft -> {
                                            sliderValue = (sliderValue - step).coerceIn(item.min, item.max)
                                            textValue = if (isMbps) "%.1f".format(sliderValue) else sliderValue.toInt().toString()
                                            true
                                        }
                                        Key.DirectionRight -> {
                                            sliderValue = (sliderValue + step).coerceIn(item.min, item.max)
                                            textValue = if (isMbps) "%.1f".format(sliderValue) else sliderValue.toInt().toString()
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
                        text = item.max.toInt().toString(),
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onValueChange(sliderValue)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun SliderDialogPreview() {
    MaterialTheme {
        SliderDialog(
            item = SettingItem.Slider(
                name = "bitrate",
                category = "Video",
                title = R.string.title_seekbar_bitrate,
                summary = R.string.summary_seekbar_bitrate,
                value = 50f,
                min = 0.5f,
                max = 150f,
                unit = R.string.suffix_seekbar_bitrate_mbps,
                onValueChange = {}
            ),
            onDismiss = {},
            onValueChange = {}
        )
    }
}

package com.limelight.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.limelight.R
import com.limelight.repository.SettingItem
import com.limelight.ui.theme.MoonlightAndroidTheme

@Composable
fun ToggleSettingListItem(settingItem: SettingItem.Toggle, onToggle: (Boolean) -> Unit) {
    ListItem(
        modifier = Modifier
            .toggleable(
                value = settingItem.default,
                onValueChange = onToggle,
                role = Role.Switch
            )
            .height(IntrinsicSize.Min),
        headlineContent = {
            Text(stringResource(settingItem.title))
        },
        supportingContent = {
            Text(stringResource(settingItem.summary))
        },
        trailingContent = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Switch(
                    checked = settingItem.default,
                    onCheckedChange = null
                )
            }
        }
    )
}

@Composable
fun SelectionSettingListItem(item: SettingItem.Selection, onClick: () -> Unit) {
    val currentEntryLabel = item.entries.getOrNull(item.entryValues.indexOf(item.currentValue)) ?: item.currentValue

    ListItem(
        modifier = Modifier
            .clickable { onClick() }
            .height(IntrinsicSize.Min),
        headlineContent = {
            Text(stringResource(item.title))
        },
        supportingContent = {
            Text(stringResource(item.summary))
        },
        trailingContent = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(currentEntryLabel)
            }
        }
    )
}

@Composable
fun SliderSettingListItem(item: SettingItem.Slider, onClick: () -> Unit) {
    val isMbps = item.unit == R.string.suffix_seekbar_bitrate_mbps
    val valueText = if (isMbps) {
        "%.1f".format(item.value)
    } else {
        item.value.toInt().toString()
    }
    val unitText = item.unit?.let { stringResource(it) } ?: ""
    val currentValueText = if (unitText.isNotEmpty()) "$valueText $unitText" else valueText

    ListItem(
        modifier = Modifier
            .clickable { onClick() }
            .height(IntrinsicSize.Min),
        headlineContent = {
            Text(stringResource(item.title))
        },
        supportingContent = {
            Text(stringResource(item.summary))
        },
        trailingContent = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(currentValueText)
            }
        }
    )
}

@Composable
fun ActionSettingListItem(item: SettingItem.Action) {
    ListItem(
        modifier = Modifier.clickable { item.onClick() },
        headlineContent = {
            Text(stringResource(item.title))
        },
        supportingContent = {
            val summary = item.summaryText ?: if (item.summary != 0) stringResource(item.summary) else null
            if (summary != null) {
                Text(summary)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingItemsPreview() {
    MoonlightAndroidTheme {
        Column {
            ToggleSettingListItem(
                settingItem = SettingItem.Toggle(
                    name = "toggle",
                    category = "category",
                    title = R.string.title_checkbox_stretch_video,
                    summary = R.string.title_checkbox_stretch_video,
                    default = true,
                    onToggle = {}
                ),
                onToggle = {}
            )
            SelectionSettingListItem(
                item = SettingItem.Selection(
                    name = "selection",
                    category = "category",
                    title = R.string.title_resolution_list,
                    summary = R.string.summary_resolution_list,
                    entries = listOf("1080p", "720p"),
                    entryValues = listOf("1080", "720"),
                    currentValue = "1080",
                    onSelected = {}
                ),
                onClick = {}
            )
            SliderSettingListItem(
                item = SettingItem.Slider(
                    name = "slider",
                    category = "category",
                    title = R.string.title_seekbar_bitrate,
                    summary = R.string.summary_seekbar_bitrate,
                    value = 50.0f,
                    min = 0.5f,
                    max = 150.0f,
                    unit = R.string.suffix_seekbar_bitrate_mbps,
                    onValueChange = {}
                ),
                onClick = {}
            )
            ActionSettingListItem(
                item = SettingItem.Action(
                    name = "action",
                    category = "category",
                    title = R.string.title_language_list,
                    summary = R.string.summary_language_list,
                    onClick = {}
                )
            )
        }
    }
}

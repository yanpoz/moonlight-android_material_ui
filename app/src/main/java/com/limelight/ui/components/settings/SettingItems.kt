package com.limelight.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.limelight.repository.SettingItem

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
    val valueText = item.value.toInt().toString()
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
            Text(stringResource(item.summary))
        }
    )
}

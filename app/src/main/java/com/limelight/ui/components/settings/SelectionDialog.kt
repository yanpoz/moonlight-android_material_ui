package com.limelight.ui.components.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.repository.SettingItem
import com.limelight.ui.components.ScrollableAlertDialog
import com.limelight.ui.theme.MoonlightAndroidTheme

@Composable
fun SelectionDialog(
    item: SettingItem.Selection,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    ScrollableAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(item.title)) },
        content = {
            Text(
                text = stringResource(item.summary),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            item.entries.forEachIndexed { index, entry ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (item.entryValues[index] == item.currentValue),
                            onClick = {
                                onSelected(item.entryValues[index])
                            },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (item.entryValues[index] == item.currentValue),
                        onClick = null
                    )
                    Text(
                        text = entry,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun SelectionDialogPreview() {
    MoonlightAndroidTheme {
        SelectionDialog(
            item = SettingItem.Selection(
                name = "resolution",
                category = "basic",
                title = R.string.title_resolution_list,
                summary = R.string.summary_resolution_list,
                entries = listOf("720p", "1080p", "4K"),
                entryValues = listOf("720", "1080", "2160"),
                currentValue = "1080",
                onSelected = {}
            ),
            onDismiss = {},
            onSelected = {}
        )
    }
}

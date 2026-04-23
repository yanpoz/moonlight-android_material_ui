package com.limelight.ui.components.dialogs

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.limelight.R

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AdaptiveDialog(
        title = { Text(text = title) },
        content = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.applist_menu_cancel))
            }
        },
        onDismissRequest = { onDismiss() },
    )
}

@Preview
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        title = "Are you sure?",
        text = "This action cannot be undone.",
        onConfirm = {},
        onDismiss = {}
    )
}

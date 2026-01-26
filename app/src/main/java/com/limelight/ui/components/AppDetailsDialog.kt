package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.limelight.computers.getAppDetails
import com.limelight.nvstream.http.NvApp

@Composable
fun AppDetailsDialog(app: NvApp, onDismiss: () -> Unit) {
    val appDetails = getAppDetails(app)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = app.appName) },
        text = {
            Column {
                appDetails.forEach { (key, value) ->
                    Text(text = "$key: $value")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss)
            { Text("OK") }
        }
    )
}

@Preview
@Composable
fun AppDetailsDialogPreview() {
    val app = NvApp().apply {
        appName = "My Awesome Game"
        appId = 12345
        isHdrSupported = true
    }
    AppDetailsDialog(app = app, onDismiss = {})
}

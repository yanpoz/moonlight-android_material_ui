package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.limelight.computers.getAppDetails
import com.limelight.nvstream.http.NvApp

@Composable
fun AppDetailsDialog(app: NvApp, onDismiss: () -> Unit) {
    val appDetails = getAppDetails(app)
    ScrollableAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = app.appName) },
        content = {
            SelectionContainer {
                Column {
                    appDetails.forEachIndexed { index, (key, value) ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = key,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = value,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (index < appDetails.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
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

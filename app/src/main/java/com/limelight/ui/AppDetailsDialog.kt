package com.limelight.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.limelight.computers.Computer
import com.limelight.nvstream.http.NvApp
import com.limelight.viewmodel.MainViewModel

@Composable
fun AppDetailsDialog(viewModel: MainViewModel, app: NvApp, computer: Computer) {
    val appDetails = viewModel.getAppDetails(app, computer)
    AlertDialog(
        onDismissRequest = { viewModel.dismissAppDetailsDialog() },
        title = { Text(text = app.appName) },
        text = {
            Column {
                appDetails.forEach { (key, value) ->
                    Text(text = "$key: $value")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.dismissAppDetailsDialog() })
            { Text("OK") }
        }
    )
}
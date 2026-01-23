package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.limelight.computers.Computer
import com.limelight.computers.getComputerDetailsText

@Composable
fun ComputerDetailsDialog(computer: Computer, onDismiss: () -> Unit) {
    val computerDetailsText = getComputerDetailsText(computer)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = computer.details.name) },
        text = {
            Column {
                computerDetailsText.forEach { (key, value) ->
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
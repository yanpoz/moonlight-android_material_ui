package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.limelight.computers.Computer
import com.limelight.computers.getComputerDetailsText
import com.limelight.viewmodel.MainViewModel

@Composable
fun ComputerDetailsDialog(viewModel: MainViewModel, computer: Computer) {
    val computerDetailsText = getComputerDetailsText(computer)
    AlertDialog(
        onDismissRequest = { viewModel.dismissComputerDetailsDialog() },
        title = { Text(text = computer.details.name) },
        text = {
            Column {
                computerDetailsText.forEach { (key, value) ->
                    Text(text = "$key: $value")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.dismissComputerDetailsDialog() })
            { Text("OK") }
        }
    )
}
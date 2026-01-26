package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.limelight.computers.Computer
import com.limelight.computers.getComputerDetailsText
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager

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

@Preview
@Composable
fun ComputerDetailsDialogPreview() {
    val computerDetails = ComputerDetails().apply {
        name = "My Gaming PC"
        state = ComputerDetails.State.ONLINE
        pairState = PairingManager.PairState.PAIRED
        macAddress = "00:11:22:33:44:55"
        localAddress = ComputerDetails.AddressTuple("192.168.1.100", 47989)
        remoteAddress = ComputerDetails.AddressTuple("123.45.67.89", 47989)
    }
    val computer = Computer(details = computerDetails)
    ComputerDetailsDialog(computer = computer, onDismiss = {})
}

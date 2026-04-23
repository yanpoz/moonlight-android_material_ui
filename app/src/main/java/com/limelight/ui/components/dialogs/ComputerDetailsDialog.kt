package com.limelight.ui.components.dialogs

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
import com.limelight.computers.Computer
import com.limelight.computers.detailsList
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager
import java.util.UUID

@Composable
fun ComputerDetailsDialog(computer: Computer, onDismiss: () -> Unit) {
    val computerDetailsText = computer.detailsList
    AdaptiveDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = computer.details.name) },
        content = {
            SelectionContainer {
                Column {
                    computerDetailsText.forEachIndexed { index, (key, value) ->
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
                                    modifier = Modifier.weight(1.7f)
                                )
                            }
                            if (index < computerDetailsText.lastIndex) {
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
fun ComputerDetailsDialogPreview() {
    val computerDetails = ComputerDetails().apply {
        name = "My Gaming PC"
        state = ComputerDetails.State.ONLINE
        pairState = PairingManager.PairState.PAIRED
        macAddress = "00:11:22:33:44:55"
        localAddress = ComputerDetails.AddressTuple("192.168.1.100", 47989)
        remoteAddress = ComputerDetails.AddressTuple("123.45.67.89", 47989)
        uuid = UUID.randomUUID().toString()
    }
    val computer = Computer(details = computerDetails)
    ComputerDetailsDialog(computer = computer, onDismiss = {})
}

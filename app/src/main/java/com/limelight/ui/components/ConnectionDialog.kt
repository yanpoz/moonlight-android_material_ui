package com.limelight.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.computers.getComputerPairPinText
import com.limelight.computers.getComputerPairStatusText
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager
import com.limelight.viewmodel.MainViewModel

@Composable
fun ConnectionDialog(computer: Computer, onConnect: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    //  TODO Add container transformation
    AlertDialog(
        title = { Text(text = "Connecting to: ${computer.details.name}") },
        text = {
            Column {
                Text(text = getComputerPairStatusText(computer))
                Text(text = getComputerPairPinText(computer))
            }
        },
        confirmButton = {
            if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                Row {
                    TextButton(onClick = { onConnect() }) { Text("Connect to Desktop") }
                    TextButton(onClick = { onDismiss() }) { Text("Back to Apps") }
                }
            } else {
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = MainViewModel.TROUBLESHOOTING_URL.toUri()
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.help))
                }
                TextButton(onClick = { onDismiss() }) { Text(stringResource(R.string.applist_menu_cancel)) }
            }
        },
        onDismissRequest = { onDismiss() },
    )
}

@Preview
@Composable
fun ConnectionDialogPairedPreview() {
    val computerDetails = ComputerDetails()
    computerDetails.name = "My Gaming PC"
    computerDetails.state = ComputerDetails.State.ONLINE
    computerDetails.localAddress = ComputerDetails.AddressTuple("192.168.1.100", 47989)
    computerDetails.remoteAddress = ComputerDetails.AddressTuple("123.45.67.89", 47989)
    computerDetails.macAddress = "00:11:22:33:44:55"
    computerDetails.runningGameId = 0
    computerDetails.pairState = PairingManager.PairState.PAIRED

    ConnectionDialog(
        computer = Computer(computerDetails),
        onConnect = {},
        onDismiss = {}
    )
}

@Preview
@Composable
fun ConnectionDialogUnpairedPreview() {
    val computerDetails = ComputerDetails()
    computerDetails.name = "My Gaming PC"
    computerDetails.state = ComputerDetails.State.ONLINE
    computerDetails.localAddress = ComputerDetails.AddressTuple("192.168.1.100", 47989)
    computerDetails.remoteAddress = ComputerDetails.AddressTuple("123.45.67.89", 47989)
    computerDetails.macAddress = "00:11:22:33:44:55"
    computerDetails.runningGameId = 0
    computerDetails.pairState = PairingManager.PairState.FAILED

    ConnectionDialog(
        computer = Computer(computerDetails),
        onConnect = {},
        onDismiss = {}
    )
}

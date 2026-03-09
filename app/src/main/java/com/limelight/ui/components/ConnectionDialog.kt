package com.limelight.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.computers.getComputerPairPinText
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.PairingManager
import com.limelight.ui.theme.VerySunnyShape
import com.limelight.viewmodel.MainViewModel

@Composable
fun ConnectionDialog(computer: Computer, onConnect: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    //  TODO Add container transformation
    AlertDialog(
        title = {
            Text(
                text = "${computer.details.name} pairing",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(VerySunnyShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Paired successfully",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.pair_pairing_msg),
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(40.dp))
                            .padding(horizontal = 32.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getComputerPairPinText(computer),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.pair_pairing_help),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { onDismiss() }) { Text("Back to Apps") }
                    TextButton(onClick = { onConnect() }) { Text("Connect to Desktop") }
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
    computerDetails.pairState = PairingManager.PairState.NOT_PAIRED

    val computer = Computer(computerDetails, pairPin = "1234")

    ConnectionDialog(
        computer = computer,
        onConnect = {},
        onDismiss = {}
    )
}

package com.limelight.ui

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.computers.getComputerPairPinText
import com.limelight.computers.getComputerPairResultText
import com.limelight.computers.getComputerPairStatusText
import com.limelight.viewmodel.MainViewModel

@Composable
fun ConnectionDialog(
    viewModel: MainViewModel, computer: Computer, onConnect: () -> Unit, onDismiss: () -> Unit
) {
    val context = LocalContext.current
    //  TODO Add container transformation
    AlertDialog(
        title = { Text(text = "Connecting to: ${computer.details.name}") },
        text = {
            Column {
                Text(text = getComputerPairStatusText(computer))
                Text(text = getComputerPairPinText(computer))
                Text(text = getComputerPairResultText(computer))
            }
        },
        confirmButton = {
            if (computer.isPaired()) {
                Row {
                    TextButton(onClick = { onConnect() }) { Text("Connect to Desktop") }
                    TextButton(onClick = { onDismiss() }) { Text("Display Apps & Games") }
                }
            } else {
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = MainViewModel.Companion.TROUBLESHOOTING_URL.toUri()
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.help))
                }
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text(stringResource(R.string.applist_menu_cancel))
                }
            }
        },
        onDismissRequest = { onDismiss() },
    )
}
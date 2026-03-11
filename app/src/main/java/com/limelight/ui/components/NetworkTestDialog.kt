package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.repository.ComputerRepository

@Composable
fun NetworkTestDialog(
    networkTestStatus: ComputerRepository.NetworkTestStatus,
    onDismiss: () -> Unit
) {
    if (networkTestStatus is ComputerRepository.NetworkTestStatus.Idle) return

    ScrollableAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (networkTestStatus is ComputerRepository.NetworkTestStatus.Running)
                    stringResource(R.string.nettest_title_waiting)
                else
                    stringResource(R.string.nettest_title_done)
            )
        },
        content = {
            if (networkTestStatus is ComputerRepository.NetworkTestStatus.Running) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.nettest_text_waiting))
                }
            } else if (networkTestStatus is ComputerRepository.NetworkTestStatus.Finished) {
                Text(networkTestStatus.result)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}

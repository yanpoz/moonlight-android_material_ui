package com.limelight.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualComputerAddDialog(
    inputIp: String,
    onInputIpChange: (String) -> Unit,
    onAddComputer: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_add_pc)) },
        text = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                value = inputIp,
                onValueChange = onInputIpChange,
                label = { Text(stringResource(R.string.ip_hint)) }
            )
        },
        confirmButton = {
            Button(
                onClick = onAddComputer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.title_add_pc))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Preview
@Composable
fun ManualComputerAddDialogPreview() {
    ManualComputerAddDialog(
        inputIp = "192.168.1.100",
        onInputIpChange = {},
        onAddComputer = {},
        onDismiss = {}
    )
}

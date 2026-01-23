package com.limelight.ui.components

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualComputerAddDialog(viewModel: MainViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.manualComputerAdding.showDialog = false },
        title = { Text(stringResource(R.string.title_add_pc)) },
        text = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                value = viewModel.manualComputerAdding.inputIp,
                onValueChange = { viewModel.manualComputerAdding.inputIp = it },
                label = { Text(stringResource(R.string.ip_hint)) }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addComputer(viewModel.manualComputerAdding.inputIp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.title_add_pc))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    viewModel.manualComputerAdding.showDialog = false
                }
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}
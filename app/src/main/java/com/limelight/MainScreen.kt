package com.limelight

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val computers = viewModel.computers


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Moonlight") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.showBottomSheet = true }) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = "https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/".toUri()
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = "Info")
                    }
                    IconButton(onClick = onSettingsClick ) {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(horizontal = 20.dp)) {
            if (computers.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.scut_pc_not_found),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn {
                    items(computers) { computer ->
                        ComputerItem(
                            computer = computer,
                            onClick = { viewModel.onComputerClicked(it) },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    if (viewModel.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(stringResource(id = R.string.title_add_pc))
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    value = viewModel.inputIp,
                    onValueChange = { viewModel.inputIp = it },
                    label = { Text(stringResource(id = R.string.ip_hint)) }
                )

                Button(
                    onClick = { viewModel.addComputer(context, viewModel.inputIp) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(stringResource(id = R.string.title_add_pc))
                }
            }
        }
    }

    if (viewModel.showConnectionDialog && viewModel.selectedComputer != null) {
        ConnectionDialog(
            viewModel = viewModel,
            computer = viewModel.selectedComputer!!,
            onDismiss = { viewModel.dismissComputerDialog() }
        )
    }
}


@Composable
fun ConnectionDialog(viewModel: MainViewModel, computer: ComputerDetails, onDismiss: () -> Unit) {
//  TODO Add container transformation
    AlertDialog(
        onDismissRequest = { viewModel.dismissComputerDialog() },
        title = { Text(text = "Connecting to: ${computer.name}") },
        text = { Text(text = viewModel.getPairStatusText(computer)) },
        confirmButton = {
            TextButton(
                onClick = { viewModel.dismissComputerDialog() }
            ) {
                Text("Close")
            }
        }
    )
}


@Composable
fun ComputerItem(
    computer: ComputerDetails, 
    onClick: (ComputerDetails) -> Unit,
    viewModel: MainViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .aspectRatio(16f / 9f)
            .padding(vertical = 8.dp)
            .clickable { onClick(computer) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = computer.name,
                    style = MaterialTheme.typography.titleLarge
                )

                // Status indicator
                val statusColor = when (computer.state) {
                    ComputerDetails.State.ONLINE -> MaterialTheme.colorScheme.primary
                    ComputerDetails.State.OFFLINE -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show IP address
            val address = computer.activeAddress?.address ?: computer.localAddress?.address
            ?: computer.remoteAddress?.address ?: computer.manualAddress?.address ?: "Unknown"

            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp)) // Added spacer

            Text(
                text = viewModel.getPairStatusText(computer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.limelight.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.limelight.computers.getComputerDetailsText
import com.limelight.computers.getComputerPairResultText
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import com.limelight.computers.Computer
import com.limelight.viewmodel.MainViewModel
import com.limelight.computers.getComputerPairPinText
import com.limelight.computers.getComputerPairStatusText
import com.limelight.ui.components.AppItem
import com.limelight.ui.components.ComputerItem
import com.limelight.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState())
    val computers by viewModel.computers.collectAsState()

    PullToRefreshBox(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.updateComputerApps() }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text("Moonlight") },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.manualComputerAdding.showDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircleOutline,
                                contentDescription = stringResource(R.string.title_add_pc)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = MainViewModel.SETUP_GUIDE_URL.toUri()
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(imageVector = Icons.Outlined.Info,
                                 contentDescription = stringResource(R.string.help))
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(imageVector = Icons.Outlined.Settings,
                                 contentDescription = "Settings")
                        }
                    },
                )
            },
        ) { paddingValues ->
            if (computers.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.scut_pc_not_found),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(computers, key = { it.details.uuid }) { computer ->
                        LazyRow(
                            modifier = Modifier
                                .height(200.dp) // Fixed height for the entire row of items
                                .padding(vertical = 16.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ComputerItem as the first item
                            item(key = computer.details.uuid) {
                                ComputerItem(
                                    computer = computer,
                                    onClick = {
                                        viewModel.onComputerInitiateConnection(
                                            context = context, computerUuid = it.details.uuid
                                        )
                                    },
                                    viewModel = viewModel,
                                    context = context,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(16f / 9f)
                                )
                            }
                            // AppItems
                            if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                                items(computer.apps, key = { it.appId }) { app ->
                                    AppItem(
                                        context = context,
                                        app = app,
                                        computer = computer,
                                        onClick = { viewModel.onLaunchApp(context, app, computer) },
                                        viewModel = viewModel,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(2f / 3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.manualComputerAdding.showDialog) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.manualComputerAdding.showDialog = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(stringResource(R.string.title_add_pc))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    value = viewModel.manualComputerAdding.inputIp,
                    onValueChange = { viewModel.manualComputerAdding.inputIp = it },
                    label = { Text(stringResource(R.string.ip_hint)) }
                )
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
            }
        }
    }

    if (viewModel.connectionDialog.showDialog && viewModel.connectionDialog.computerUuid != null) {
        val computer = computers.find { it.details.uuid == viewModel.connectionDialog.computerUuid }
        if (computer != null) {
            ConnectionDialog(
                viewModel = viewModel,
                computer = computer,
                onConnect = { viewModel.onComputerInitiateConnection(
                    context = context,
                    computerUuid = computer.details.uuid)
                },
                onDismiss = { viewModel.dismissConnectionDialog() }
            )
        }
    }

    if (viewModel.appViewDetails.showDialog) {
        AppDetailsDialog(
            viewModel = viewModel,
            app = viewModel.appViewDetails.app!!,
            computer = viewModel.appViewDetails.computer!!,
        )
    }
    
    if (viewModel.computerViewDetails.showDialog) {
        ComputerDetailsDialog(
            viewModel = viewModel,
            computer = viewModel.computerViewDetails.computer!!,
        )
    }

    if (viewModel.confirmationDialog.showDialog) {
        ConfirmationDialog(
            title = viewModel.confirmationDialog.title,
            text = viewModel.confirmationDialog.text,
            onConfirm = {
                viewModel.confirmationDialog.action()
                viewModel.dismissConfirmationDialog()
            },
            onDismiss = { viewModel.dismissConfirmationDialog() }
        )
    }
}

@Composable
fun AppDetailsDialog(viewModel: MainViewModel, app: NvApp, computer: Computer) {
    val appDetails = viewModel.getAppDetails(app, computer)
    AlertDialog(
        onDismissRequest = { viewModel.dismissAppDetailsDialog() },
        title = { Text(text = app.appName) },
        text = {
            Column {
                appDetails.forEach { (key, value) ->
                    Text(text = "$key: $value")
                }
            }
        },
        confirmButton = {
            TextButton( onClick = { viewModel.dismissAppDetailsDialog() } )
            { Text("OK") }
        }
    )
}

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
            TextButton( onClick = { viewModel.dismissComputerDetailsDialog() } )
            { Text("OK") }
        }
    )
}


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
                    TextButton(onClick = { onConnect() } ) { Text("Connect to Desktop") }
                    TextButton(onClick = { onDismiss() } ) { Text("Display Apps & Games") }
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


@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = { onConfirm() } ) {
                Text("Confirm") // TODO should be specific
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() } ) {
                Text(stringResource(R.string.applist_menu_cancel))
            }
        },
        onDismissRequest = { onDismiss() },
    )
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // NOTE: You will need to replace MainViewModel() with a proper mock instance 
    // that provides dummy data for your preview.
    MainScreen(viewModel = MainViewModel(), onSettingsClick = {})
}

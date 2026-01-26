package com.limelight.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.limelight.R
import com.limelight.nvstream.http.PairingManager
import com.limelight.ui.components.AppDetailsDialog
import com.limelight.ui.components.AppItem
import com.limelight.ui.components.ComputerDetailsDialog
import com.limelight.ui.components.ComputerItem
import com.limelight.ui.components.ConfirmationDialog
import com.limelight.ui.components.ConnectionDialog
import com.limelight.ui.components.ManualComputerAddDialog
import com.limelight.viewmodel.MainViewModel
import com.limelight.viewmodel.MockMainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val computers by viewModel.computers.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
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
                            onClick = { viewModel.manualComputerAddHandler.onShowDialog() }
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
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.help)
                            )
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
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
                                .height(200.dp) // Fixed height for the row of items
                                .padding(vertical = 16.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ComputerItem as the first item
                            item(key = computer.details.uuid) {
                                ComputerItem(
                                    computer = computer,
                                    isMenuExpanded = viewModel.computerItemHandler.isMenuExpanded(computer.details.uuid),
                                    onDismissMenu = { viewModel.computerItemHandler.onDismissMenu() },
                                    onSendWakeOnLan = { viewModel.computerItemHandler.onSendWakeOnLan(context, computer.details.uuid) },
                                    onQuitRunningApp = { viewModel.computerItemHandler.onQuitRunningApp(context, computer) },
                                    onComputerDetailsClicked = { viewModel.computerItemHandler.onViewDetailsClicked(computer) },
                                    onClick = { viewModel.connectionHandler.onInitiateConnection(context, computer.details.uuid) },
                                    onLongClick = { viewModel.computerItemHandler.onOpenMenu(computer.details.uuid) },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(16f / 9f)
                                )
                            }
                            // AppItems
                            if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                                items(computer.apps, key = { it.appId }) { app ->
                                    AppItem(
                                        app = app,
                                        runningGameId = computer.details.runningGameId,
                                        isMenuExpanded = viewModel.appItemHandler.isMenuExpanded(app.appId, computer.details.uuid),
                                        onDismissMenu = { viewModel.appItemHandler.onDismissMenu() },
                                        onQuitApp = { viewModel.appItemHandler.onQuitApp(context, app, computer.details.uuid) },
                                        onAppDetailsClicked = { viewModel.appItemHandler.onDetailsClicked(app) },
                                        onClick = { viewModel.connectionHandler.onLaunchApp(context, app, computer.details.uuid) },
                                        onLongClick = { viewModel.appItemHandler.onOpenMenu(app.appId, computer.details.uuid) },
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

    if (viewModel.manualComputerAddHandler.uiState.showDialog) {
        ManualComputerAddDialog(
            inputIp = viewModel.manualComputerAddHandler.uiState.inputIp,
            onInputIpChange = { viewModel.manualComputerAddHandler.onInputChanged(it) },
            onAddComputer = { viewModel.manualComputerAddHandler.onManualAddComputer() },
            onDismiss = { viewModel.manualComputerAddHandler.onDismissDialog() },
        )
    }

    if (viewModel.connectionHandler.uiState.showDialog && viewModel.connectionHandler.uiState.computerUuid != null) {
        val computer = computers.find { it.details.uuid == viewModel.connectionHandler.uiState.computerUuid }
        if (computer != null) {
            ConnectionDialog(
                computer,
                onConnect = { viewModel.connectionHandler.onInitiateConnection(context, computer.details.uuid) },
                onDismiss = { viewModel.connectionHandler.onCancelConnection() }
            )
        }
    }

    if (viewModel.appItemHandler.viewDetails.showDialog) {
        AppDetailsDialog(
            app = viewModel.appItemHandler.viewDetails.app!!,
            onDismiss = { viewModel.appItemHandler.onDismissDetailsDialog() },
        )
    }

    if (viewModel.computerItemHandler.viewDetails.showDialog) {
        ComputerDetailsDialog(
            computer = viewModel.computerItemHandler.viewDetails.computer!!,
            onDismiss = { viewModel.computerItemHandler.onDismissDetailsDialog() },
        )
    }

    if (viewModel.confirmationHandler.uiState.showDialog) {
        ConfirmationDialog(
            title = viewModel.confirmationHandler.uiState.title,
            text = viewModel.confirmationHandler.uiState.text,
            onConfirm = {
                viewModel.confirmationHandler.uiState.action()
                viewModel.confirmationHandler.dismissDialog()
            },
            onDismiss = { viewModel.confirmationHandler.dismissDialog() }
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(viewModel = MockMainViewModel()) {}
}

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
import com.limelight.nvstream.http.PairingManager
import com.limelight.viewmodel.MainViewModel
import com.limelight.ui.components.AppItem
import com.limelight.ui.components.ComputerItem
import com.limelight.R
import com.limelight.ui.components.AppDetailsDialog
import com.limelight.ui.components.ComputerDetailsDialog
import com.limelight.ui.components.ConfirmationDialog
import com.limelight.ui.components.ConnectionDialog
import com.limelight.ui.components.ManualComputerAddDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
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
                                    viewModel, computer, context,
                                    onClick = { viewModel.computerInitiateConnection(
                                        context, computer.details.uuid
                                    )},
                                    onLongClick = { viewModel.computerOpenMenu(
                                        computer.details.uuid
                                    )},
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(16f / 9f)
                                )
                            }
                            // AppItems
                            if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                                items(computer.apps, key = { it.appId }) { app ->
                                    AppItem(
                                        viewModel, app, computer, context,
                                        onClick = { viewModel.onLaunchApp(
                                            context, app, computer.details.uuid
                                        )},
                                        onLongClick = { viewModel.appOpenMenu(
                                            app.appId, computer.details.uuid
                                        )},
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
        ManualComputerAddDialog(viewModel)
    }

    if (viewModel.connectionDialog.showDialog && viewModel.connectionDialog.computerUuid != null) {
        val computer = computers.find { it.details.uuid == viewModel.connectionDialog.computerUuid }
        if (computer != null) {
            ConnectionDialog(
                viewModel, computer,
                onConnect = {
                    viewModel.computerInitiateConnection(
                        context = context,
                        computerUuid = computer.details.uuid
                    )
                },
                onDismiss = { viewModel.dismissConnectionDialog() }
            )
        }
    }

    if (viewModel.appViewDetails.showDialog) {
        AppDetailsDialog(
            viewModel,
            app = viewModel.appViewDetails.app!!,
            computer = viewModel.appViewDetails.computer!!,
        )
    }
    
    if (viewModel.computerViewDetails.showDialog) {
        ComputerDetailsDialog(
            viewModel,
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


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // NOTE: You will need to replace MainViewModel() with a proper mock instance 
    // that provides dummy data for your preview.
    MainScreen(viewModel = MainViewModel(), onSettingsClick = {})
}

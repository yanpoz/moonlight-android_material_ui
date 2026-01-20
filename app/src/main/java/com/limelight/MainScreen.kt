package com.limelight

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import com.limelight.repository.Computer
import com.limelight.viewmodel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val computers = viewModel.computers
    val isRefreshing = viewModel.isRefreshing

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.updateApps() }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text("Moonlight") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.showBottomSheet = true }) {
                            Icon(imageVector = Icons.Outlined.AddCircleOutline,
                                 contentDescription = stringResource(R.string.title_add_pc))
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://github.com/moonlight-stream/moonlight-docs/wiki/Setup-Guide/".toUri()
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                 contentDescription = stringResource(R.string.help))
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(imageVector = Icons.Outlined.Settings,
                                 contentDescription = "Settings") //TODO: add string resource
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
                                    onClick = { viewModel.onComputerConnect(context, it.details.uuid) },
                                    viewModel = viewModel,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(16f / 9f)
                                )
                            }

                            // AppItems
                            items(computer.apps, key = { it.appId }) { app ->
                                AppItem(
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

    if (viewModel.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(stringResource(R.string.title_add_pc))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    value = viewModel.inputIp,
                    onValueChange = { viewModel.inputIp = it },
                    label = { Text(stringResource(R.string.ip_hint)) }
                )

                Button(
                    onClick = { viewModel.addComputer(viewModel.inputIp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(stringResource(R.string.title_add_pc))
                }
            }
        }
    }

    if (viewModel.showConnectionDialog && viewModel.selectedComputer != null) {
        ConnectionDialog(
            viewModel = viewModel,
            computer = viewModel.selectedComputer!!,
            onConnect = { viewModel.onComputerConnect(context, viewModel.selectedComputerUUID!!) },
            onDismiss = { viewModel.dismissConnectionDialog() }
        )
    }

    if (viewModel.showAppDetailsDialog) {
        AppDetailsDialog(
            viewModel = viewModel,
            app = viewModel.selectedApp!!,
            computer = viewModel.selectedComputerForApp!!,
        )
    }
}

@Composable
fun AppDetailsDialog(viewModel: MainViewModel, app: NvApp, computer: Computer) {
    val details = viewModel.getAppDetails(app, computer)
    AlertDialog(
        onDismissRequest = { viewModel.dismissAppDetailsDialog() },
        title = { Text(text = app.appName) },
        text = {
            Column {
                details.forEach { (key, value) ->
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
fun ConnectionDialog(viewModel: MainViewModel, computer: Computer, onConnect: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    //  TODO Add container transformation
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Connecting to: ${computer.details.name}") },
        text = {
            Column {
                Text(text = viewModel.getPairStatusText(computer))
                Text(text = viewModel.getPairPinText(computer))
                Text(text = viewModel.getPairResultText(computer))
            }
        },
        confirmButton = {
            if (viewModel.isComputerPaired(computer)) {
                Row {
                    TextButton(onClick = { onConnect() } ) { Text("Connect to Desktop") }
                    TextButton(onClick = { onDismiss() } ) { Text("Display Apps & Games") }
                }
            } else {
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = "https://github.com/moonlight-stream/moonlight-docs/wiki/Troubleshooting".toUri()
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
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComputerItem(
    computer: Computer,
    onClick: (Computer) -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(16f / 9f)
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { onClick(computer) },
                onLongClick = { viewModel.onComputerLongPress(computer.details.uuid) }
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = computer.details.name,
                    style = MaterialTheme.typography.titleLarge
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(viewModel.getStatusColor(computer), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = viewModel.getPairStatusText(computer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            modifier = Modifier.widthIn(min = 192.dp),
            // TODO add caption
            expanded = viewModel.expandedMenuComputerUuid == computer.details.uuid,
            onDismissRequest = { viewModel.dismissComputerMenu() }
        ) {
            if (computer.details.state == ComputerDetails.State.OFFLINE ||
                computer.details.state == ComputerDetails.State.UNKNOWN) {
                // Send Wake-On-LAN
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pcview_menu_send_wol)) },
                    leadingIcon = { Icon(Icons.Outlined.PowerSettingsNew, null) },
                    onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
                )
            }
            else if (computer.details.pairState != PairingManager.PairState.PAIRED) {
                // Pair PC
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pcview_menu_pair_pc)) },
                    leadingIcon = { Icon(Icons.Outlined.Handshake, null) },
                    onClick = {
                        viewModel.dismissComputerMenu()
                        onClick(computer)
                    }
                )
            }
            else {
                if (computer.details.runningGameId != 0) {
                    // Resume Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_resume)) },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = {
                            viewModel.dismissComputerMenu()
                            onClick(computer)
                        }
                    )
                    // Quit Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit)) },
                        leadingIcon = { Icon(Icons.Outlined.Close, null) },
                        onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
                    )
                }
            }
            HorizontalDivider() // TODO: replace with gap Material expressive
            // Move Up TODO: should not be available when on top
            DropdownMenuItem(
                text = { Text(text = "Move Up") }, // TODO: Add string resource
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowUp, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            // Move Down TODO: should not be available when on bottom
            DropdownMenuItem(
                text = { Text(text = "Move Down") }, // TODO: Add string resource
                leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            HorizontalDivider() // TODO: replace with gap Material expressive
            // Test Network Connection
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_test_network)) },
                leadingIcon = { Icon(Icons.Outlined.Speed, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            // Create shortcut
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_scut)) },
                leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
                onClick = { viewModel.dismissAppMenu() /*TODO*/ }
            )
            // View Details
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_details)) },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            // Delete PC
            DropdownMenuItem(
                text = { Text(stringResource(R.string.pcview_menu_delete_pc), color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Delete,
                                     tint = MaterialTheme.colorScheme.error,
                                     contentDescription = null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(
    app: NvApp,
    computer: Computer,
    onClick: () -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(2f / 3f) // Vertical card (3:2 height:width)
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { viewModel.onAppLongPress(computer.details.uuid, app.appId) }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom // Align app name to the bottom
        ) {
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        DropdownMenu(
            modifier = Modifier.widthIn(min = 192.dp),
            expanded = viewModel.expandedMenuAppId == app.appId &&
                       viewModel.expandedMenuComputerUuidForApp == computer.details.uuid,
            onDismissRequest = { viewModel.dismissAppMenu() }
        ) {
            if (viewModel.lastRunningAppId != 0) {
                if (viewModel.lastRunningAppId == app.appId) {
                    // Resume Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_resume)) },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = { viewModel.dismissAppMenu() /*TODO*/ }
                    )
                    // Quit Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_quit_app)) },
                        leadingIcon = { Icon(Icons.Outlined.Close, null) },
                        onClick = { viewModel.dismissAppMenu() /*TODO*/ }
                    )
                }
                else {
                    // Quit running and Start new session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit_and_start)) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null) },
                        onClick = { viewModel.dismissAppMenu() /*TODO*/ }
                    )
                }
            }
            HorizontalDivider() // TODO: replace with gap Material expressive
            // Move Up TODO: should not be available when on beginning
            DropdownMenuItem(
                text = { Text(text = "Move Left") }, // TODO: Add string resource AutoMirrored (?)
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            // Move Down TODO: should not be available when on bottom
            DropdownMenuItem(
                text = { Text(text = "Move Right") }, // TODO: Add string resource AutoMirrored (?)
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            HorizontalDivider() // TODO: replace with gap Material expressive
            // Hide App
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_hide_app)) },
                leadingIcon = { Icon(Icons.Outlined.VisibilityOff, null) },
                onClick = { viewModel.dismissAppMenu() /*TODO*/ }
            )
            // App Details
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_details)) },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, null) },
                onClick = {
                    viewModel.dismissAppMenu()
                    viewModel.onAppDetailsClicked(computer, app)
                }
            )
            // Create shortcut
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_scut)) },
                leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
                onClick = { viewModel.dismissAppMenu() /*TODO*/ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // NOTE: You will need to replace MainViewModel() with a proper mock instance 
    // that provides dummy data for your preview.
    MainScreen(viewModel = MainViewModel(), onSettingsClick = {})
}

package com.limelight.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.limelight.R
import com.limelight.grid.assets.CachedAppAssetLoader
import com.limelight.grid.assets.DiskAssetLoader
import com.limelight.grid.assets.MemoryAssetLoader
import com.limelight.grid.assets.NetworkAssetLoader
import com.limelight.nvstream.http.PairingManager
import com.limelight.preferences.PreferenceConfiguration
import com.limelight.ui.components.AppDetailsDialog
import com.limelight.ui.components.AppItemCard
import com.limelight.ui.components.ComputerDetailsDialog
import com.limelight.ui.components.ComputerItemCard
import com.limelight.ui.components.ConfirmationDialog
import com.limelight.ui.components.ConnectionDialog
import com.limelight.ui.components.ManualComputerAddDialog
import com.limelight.ui.components.QuickSettingsDialog
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
    val uniqueId by viewModel.uniqueId.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

// TODO: return pull to refresh when 'enabled' property is added to PullToRefreshBox
// https://issuetracker.google.com/issues/369044003
//    PullToRefreshBox(
//        isRefreshing = isRefreshing,
//        onRefresh = { viewModel.updateComputerApps() }
//    ) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Moonlight") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.manualComputerAddHandler.onShowDialog() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = stringResource(R.string.title_add_pc)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                            val currentFps = prefs.getString(
                                PreferenceConfiguration.FPS_PREF_STRING,
                                PreferenceConfiguration.DEFAULT_FPS
                            ) ?: PreferenceConfiguration.DEFAULT_FPS
                            val currentRes = prefs.getString(
                                PreferenceConfiguration.RESOLUTION_PREF_STRING,
                                PreferenceConfiguration.DEFAULT_RESOLUTION
                            ) ?: PreferenceConfiguration.DEFAULT_RESOLUTION
                            val currentBitrate = prefs.getInt(
                                PreferenceConfiguration.BITRATE_PREF_STRING,
                                PreferenceConfiguration.getDefaultBitrate(context)
                            ).toFloat() / 1000f
                            val touchscreenTrackpad = prefs.getBoolean(
                                PreferenceConfiguration.TOUCHSCREEN_TRACKPAD_PREF_STRING,
                                PreferenceConfiguration.DEFAULT_TOUCHSCREEN_TRACKPAD
                            )
                            val onscreenController = prefs.getBoolean(
                                PreferenceConfiguration.ONSCREEN_CONTROLLER_PREF_STRING,
                                PreferenceConfiguration.ONSCREEN_CONTROLLER_DEFAULT
                            )
                            val hostAudio = prefs.getBoolean(
                                PreferenceConfiguration.HOST_AUDIO_PREF_STRING,
                                PreferenceConfiguration.DEFAULT_HOST_AUDIO
                            )
                            val mouseEmulation = prefs.getBoolean(
                                PreferenceConfiguration.MOUSE_EMULATION_STRING,
                                PreferenceConfiguration.DEFAULT_MOUSE_EMULATION
                            )
                            val vibrateOsc = prefs.getBoolean(
                                PreferenceConfiguration.VIBRATE_OSC_PREF_STRING,
                                PreferenceConfiguration.DEFAULT_VIBRATE_OSC
                            )
                            viewModel.quickSettingsHandler.onShowQuickSettings(
                                currentFps,
                                currentRes,
                                currentBitrate,
                                touchscreenTrackpad,
                                onscreenController,
                                hostAudio,
                                mouseEmulation,
                                vibrateOsc
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Quick Settings"
                        )
                    }
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = MainViewModel.SETUP_GUIDE_URL.toUri()
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.help)
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
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
                    val assetLoader = remember(computer.details, uniqueId) {
                        val ART_WIDTH_PX = 300
                        val LARGE_WIDTH_DP = 150
                        val dpi = context.resources.displayMetrics.densityDpi
                        val dp = LARGE_WIDTH_DP
                        var scalingDivisor = ART_WIDTH_PX / (dp * (dpi / 160.0))
                        if (scalingDivisor < 1.0) {
                            scalingDivisor = 1.0
                        }

                        CachedAppAssetLoader(
                            computer.details,
                            scalingDivisor,
                            NetworkAssetLoader(context, uniqueId ?: ""),
                            MemoryAssetLoader(),
                            DiskAssetLoader(context),
                            BitmapFactory.decodeResource(
                                context.resources, R.drawable.no_app_image)
                        )
                    }

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
                            ComputerItemCard(
                                computer = computer,
                                isMenuExpanded = viewModel.computerItemHandler.isMenuExpanded(
                                    computer.details.uuid),
                                onDismissMenu = { viewModel.computerItemHandler.onDismissMenu() },
                                onSendWakeOnLan = { viewModel.computerItemHandler.onSendWakeOnLan(
                                    context, computer.details.uuid) },
                                onQuitRunningApp = { viewModel.computerItemHandler.onQuitRunningApp(
                                    context, computer) },
                                onComputerDetailsClicked = {
                                    viewModel.computerItemHandler.onViewDetailsClicked(computer) },
                                onMoveUp = { viewModel.computerItemHandler.onMoveUp(computer.details.uuid) },
                                onMoveDown = { viewModel.computerItemHandler.onMoveDown(computer.details.uuid) },
                                onClick = { viewModel.connectionHandler.onInitiateConnection(
                                    context, computer.details.uuid) },
                                onLongClick = { viewModel.computerItemHandler.onOpenMenu(
                                    computer.details.uuid) },
                                canMoveUp = computer != computers.first(),
                                canMoveDown = computer != computers.last(),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(16f / 9f)
                                    .then(if (computers.first() == computer)
                                        Modifier.focusRequester(focusRequester) else Modifier)
                            )
                        }
                        // AppItems
                        if (computer.details.pairState == PairingManager.PairState.PAIRED) {
                            items(computer.apps, key = { it.appId }) { app ->
                                AppItemCard(
                                    app = app,
                                    assetLoader = assetLoader,
                                    runningGameId = computer.details.runningGameId,
                                    isMenuExpanded = viewModel.appItemHandler.isMenuExpanded(
                                        app.appId, computer.details.uuid),
                                    onDismissMenu = { viewModel.appItemHandler.onDismissMenu() },
                                    onQuitApp = { viewModel.appItemHandler.onQuitApp(
                                        context, app, computer.details.uuid) },
                                    onAppDetailsClicked = { viewModel.appItemHandler.onDetailsClicked(app) },
                                    onClick = { viewModel.connectionHandler.onLaunchApp(
                                        context, app, computer.details.uuid) },
                                    onLongClick = { viewModel.appItemHandler.onOpenMenu(
                                        app.appId, computer.details.uuid) },
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(2f / 3f)
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(computers) {
                if (computers.isNotEmpty()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
//    }

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
                onConnect = { viewModel.connectionHandler.onInitiateConnection(
                    context, computer.details.uuid) },
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
        viewModel.computerItemHandler.viewDetails.computer?.let { computer ->
            ComputerDetailsDialog(
                computer = computer,
                onDismiss = { viewModel.computerItemHandler.onDismissDetailsDialog() },
            )
        }
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

    if (viewModel.quickSettingsHandler.uiState.showDialog) {
        QuickSettingsDialog(
            fps = viewModel.quickSettingsHandler.uiState.fps,
            onFpsChanged = { viewModel.quickSettingsHandler.onFpsChanged(context, it) },
            resolution = viewModel.quickSettingsHandler.uiState.resolution,
            onResolutionChanged = { viewModel.quickSettingsHandler.onResolutionChanged(context, it) },
            bitrate = viewModel.quickSettingsHandler.uiState.bitrate,
            onBitrateChanged = { viewModel.quickSettingsHandler.onBitrateChanged(context, it) },
            touchscreenTrackpad = viewModel.quickSettingsHandler.uiState.touchscreenTrackpad,
            onTouchscreenTrackpadChanged = { viewModel.quickSettingsHandler.onTouchscreenTrackpadChanged(context, it) },
            onscreenController = viewModel.quickSettingsHandler.uiState.onscreenController,
            onOnscreenControllerChanged = { viewModel.quickSettingsHandler.onOnscreenControllerChanged(context, it) },
            hostAudio = viewModel.quickSettingsHandler.uiState.hostAudio,
            onHostAudioChanged = { viewModel.quickSettingsHandler.onHostAudioChanged(context, it) },
            mouseEmulation = viewModel.quickSettingsHandler.uiState.mouseEmulation,
            onMouseEmulationChanged = { viewModel.quickSettingsHandler.onMouseEmulationChanged(context, it) },
            vibrateOsc = viewModel.quickSettingsHandler.uiState.vibrateOsc,
            onVibrateOscChanged = { viewModel.quickSettingsHandler.onVibrateOscChanged(context, it) },
            onDismiss = { viewModel.quickSettingsHandler.onDismissQuickSettings() }
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(viewModel = MockMainViewModel()) {}
}

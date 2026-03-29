package com.limelight.ui

import android.content.Intent
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import com.limelight.preferences.PreferenceConfiguration
import com.limelight.ui.components.MainTopAppBar
import com.limelight.ui.theme.MoonlightAndroidTheme
import com.limelight.viewmodel.MainScreenActions
import com.limelight.viewmodel.MainScreenUiState
import com.limelight.viewmodel.MainViewModel
import java.util.UUID


/**
 * Stateful version of the Main Screen.
 * Collects state from the ViewModel and passes it to the stateless MainScreenContent.
 */
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember(viewModel, context, onSettingsClick) {
        MainScreenActions(
            onSettingsClick = onSettingsClick,
            onHelpClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = MainViewModel.SETUP_GUIDE_URL.toUri()
                }
                context.startActivity(intent)
            },
            onRefresh = { viewModel.updateComputerApps() },
            onShowManualAddDialog = { viewModel.manualComputerAddHandler.onShowDialog() },
            onManualComputerAddInputChanged = { viewModel.manualComputerAddHandler.onInputChanged(it) },
            onManualComputerAddConfirm = { viewModel.manualComputerAddHandler.onManualAddComputer() },
            onManualComputerAddDismiss = { viewModel.manualComputerAddHandler.onDismissDialog() },
            onShowQuickSettings = {
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
            },
            onQuickSettingsFpsChanged = { viewModel.quickSettingsHandler.onFpsChanged(context, it) },
            onQuickSettingsResolutionChanged = { viewModel.quickSettingsHandler.onResolutionChanged(context, it) },
            onQuickSettingsBitrateChanged = { viewModel.quickSettingsHandler.onBitrateChanged(context, it) },
            onQuickSettingsTouchscreenTrackpadChanged = { viewModel.quickSettingsHandler.onTouchscreenTrackpadChanged(context, it) },
            onQuickSettingsOnscreenControllerChanged = { viewModel.quickSettingsHandler.onOnscreenControllerChanged(context, it) },
            onQuickSettingsHostAudioChanged = { viewModel.quickSettingsHandler.onHostAudioChanged(context, it) },
            onQuickSettingsMouseEmulationChanged = { viewModel.quickSettingsHandler.onMouseEmulationChanged(context, it) },
            onQuickSettingsVibrateOscChanged = { viewModel.quickSettingsHandler.onVibrateOscChanged(context, it) },
            onQuickSettingsDismiss = { viewModel.quickSettingsHandler.onDismissQuickSettings() },
            onConnectionInitiate = { viewModel.connectionHandler.onInitiateConnection(context, it) },
            onConnectionCancel = { viewModel.connectionHandler.onCancelConnection() },
            onLaunchApp = { app, computerUuid -> viewModel.connectionHandler.onLaunchApp(context, app, computerUuid) },
            onAppMenuOpen = { appId, computerUuid -> viewModel.appItemHandler.onOpenMenu(appId, computerUuid) },
            onAppMenuDismiss = { viewModel.appItemHandler.onDismissMenu() },
            onAppQuit = { app, computerUuid -> viewModel.appItemHandler.onQuitApp(context, app, computerUuid) },
            onAppDetailsClick = { viewModel.appItemHandler.onDetailsClicked(it) },
            onAppDetailsDismiss = { viewModel.appItemHandler.onDismissDetailsDialog() },
            onAppMoveUp = { computerUuid, appId -> viewModel.appItemHandler.onMoveUp(computerUuid, appId) },
            onAppMoveDown = { computerUuid, appId -> viewModel.appItemHandler.onMoveDown(computerUuid, appId) },
            onComputerMenuOpen = { viewModel.computerItemHandler.onOpenMenu(it) },
            onComputerMenuDismiss = { viewModel.computerItemHandler.onDismissMenu() },
            onComputerDetailsClick = { viewModel.computerItemHandler.onViewDetailsClicked(it) },
            onComputerDetailsDismiss = { viewModel.computerItemHandler.onDismissDetailsDialog() },
            onComputerQuitRunningApp = { viewModel.computerItemHandler.onQuitRunningApp(context, it) },
            onComputerWakeOnLan = { viewModel.computerItemHandler.onSendWakeOnLan(context, it) },
            onComputerMoveUp = { viewModel.computerItemHandler.onMoveUp(it) },
            onComputerMoveDown = { viewModel.computerItemHandler.onMoveDown(it) },
            onComputerDelete = { viewModel.computerItemHandler.onDeleteComputer(it) },
            onComputerTestNetwork = { viewModel.computerItemHandler.onTestNetwork(context) },
            onComputerDismissNetworkTest = { viewModel.computerItemHandler.onDismissNetworkTest() },
            onConfirmationConfirm = {
                viewModel.confirmationHandler.uiState.action()
                viewModel.confirmationHandler.dismissDialog()
            },
            onConfirmationDismiss = { viewModel.confirmationHandler.dismissDialog() }
        )
    }

    MainScreenContent(
        uiState = uiState,
        actions = actions
    )
}

/**
 * Stateless version of the Main Screen.
 * Renders based on the provided state and triggers events via lambdas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    uiState: MainScreenUiState,
    actions: MainScreenActions,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val focusRequester = remember { FocusRequester() }

// TODO: return pull to refresh when 'enabled' property is added to PullToRefreshBox
// https://issuetracker.google.com/issues/369044003
//    PullToRefreshBox(
//        isRefreshing = state.isRefreshing,
//        onRefresh = actions.onRefresh
//    ) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                scrollBehavior = scrollBehavior,
                onShowManualAddDialog = actions.onShowManualAddDialog,
                onShowQuickSettings = actions.onShowQuickSettings,
                onHelpClick = actions.onHelpClick,
                onSettingsClick = actions.onSettingsClick
            )
        },
    ) { paddingValues ->
        if (uiState.computers.isEmpty()) {
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
                items(uiState.computers, key = { it.details.uuid }) { computer ->
                    ComputerRow(
                        computer = computer,
                        uniqueId = uiState.uniqueId,
                        computerMenuUiState = uiState.computerMenuUiState,
                        appMenuUiState = uiState.appMenuUiState,
                        actions = actions,
                        isFirstComputer = computer == uiState.computers.first(),
                        isLastComputer = computer == uiState.computers.last(),
                        focusRequester = focusRequester
                    )
                }
            }

            LaunchedEffect(uiState.computers) {
                if (uiState.computers.isNotEmpty()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
//    }

    MainScreenDialogs(
        uiState = uiState,
        actions = actions
    )
}


/**
 * Preview for the Main Screen showing both Light and Dark themes side-by-side.
 */
@Preview(showBackground = true, widthDp = 680)
@Composable
fun MainScreenPreview() {
    val computers = remember {
        val apps = listOf(
            NvApp("App 1", 1, false),
            NvApp("App 2", 2, true)
        )

        val details1 = ComputerDetails().apply {
            state = ComputerDetails.State.ONLINE
            uuid = UUID.randomUUID().toString()
            pairState = PairingManager.PairState.PAIRED
            activeAddress = ComputerDetails.AddressTuple("192.168.1.100", 47989)
            name = "Gaming PC"
            runningGameId = 2
        }

        val details2 = ComputerDetails().apply {
            uuid = UUID.randomUUID().toString()
            pairState = PairingManager.PairState.NOT_PAIRED
            activeAddress = ComputerDetails.AddressTuple("192.168.1.101", 47989)
            name = "Workstation"
        }

        listOf(
            Computer(details = details1, apps = apps),
            Computer(details = details2, apps = emptyList())
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("light", "dark").forEach { theme ->
            Box(modifier = Modifier.weight(1f)) {
                MoonlightAndroidTheme(theme = theme, dynamicColor = false) {
                    MainScreenContent(
                        uiState = MainScreenUiState(
                            computers = computers,
                        ),
                        actions = MainScreenActions()
                    )
                }
            }
        }
    }
}

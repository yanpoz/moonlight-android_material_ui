package com.limelight.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.limelight.computers.Computer
import com.limelight.computers.toUiState
import com.limelight.grid.assets.CachedAppAssetLoader
import com.limelight.grid.assets.DiskAssetLoader
import com.limelight.grid.assets.MemoryAssetLoader
import com.limelight.grid.assets.NetworkAssetLoader
import com.limelight.nvstream.http.ComputerDetails
import com.limelight.nvstream.http.NvApp
import com.limelight.nvstream.http.PairingManager
import com.limelight.preferences.PreferenceConfiguration
import com.limelight.repository.ComputerRepository
import com.limelight.ui.components.AppItemCard
import com.limelight.ui.components.ComputerItemCard
import com.limelight.ui.components.MainTopAppBar
import com.limelight.ui.theme.MoonlightAndroidTheme
import com.limelight.viewmodel.MainViewModel
import com.limelight.viewmodel.components.AppMenuUiState
import com.limelight.viewmodel.components.AppViewDetailsUiState
import com.limelight.viewmodel.components.ComputerMenuUiState
import com.limelight.viewmodel.components.ComputerViewDetailsUiState
import com.limelight.viewmodel.components.ConfirmationDialogUiState
import com.limelight.viewmodel.components.ConnectionDialogUiState
import com.limelight.viewmodel.components.ManualComputerAddingUiState
import com.limelight.viewmodel.components.NetworkTestUiState
import com.limelight.viewmodel.components.QuickSettingsUiState
import java.util.UUID

/**
 * UI State for the Main Screen.
 * Contains all the data required to render the MainScreenContent.
 */
data class MainScreenUiState(
    val computers: List<Computer> = emptyList(),
    val isRefreshing: Boolean = false,
    val uniqueId: String? = null,
    val networkTestStatus: ComputerRepository.NetworkTestStatus = ComputerRepository.NetworkTestStatus.Idle,
    val manualComputerAddUiState: ManualComputerAddingUiState = ManualComputerAddingUiState(),
    val connectionUiState: ConnectionDialogUiState = ConnectionDialogUiState(),
    val appMenuUiState: AppMenuUiState = AppMenuUiState(),
    val appViewDetailsUiState: AppViewDetailsUiState = AppViewDetailsUiState(),
    val computerMenuUiState: ComputerMenuUiState = ComputerMenuUiState(),
    val computerViewDetailsUiState: ComputerViewDetailsUiState = ComputerViewDetailsUiState(),
    val networkTestUiState: NetworkTestUiState = NetworkTestUiState(),
    val confirmationUiState: ConfirmationDialogUiState = ConfirmationDialogUiState(),
    val quickSettingsUiState: QuickSettingsUiState = QuickSettingsUiState()
)

/**
 * Actions for the Main Screen.
 * Holds all event handlers to keep the screen composable clean.
 */
data class MainScreenActions(
    val onSettingsClick: () -> Unit = {},
    val onHelpClick: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    // Manual Computer Add
    val onShowManualAddDialog: () -> Unit = {},
    val onManualComputerAddInputChanged: (String) -> Unit = {},
    val onManualComputerAddConfirm: () -> Unit = {},
    val onManualComputerAddDismiss: () -> Unit = {},
    // Quick Settings
    val onShowQuickSettings: () -> Unit = {},
    val onQuickSettingsFpsChanged: (String) -> Unit = {},
    val onQuickSettingsResolutionChanged: (String) -> Unit = {},
    val onQuickSettingsBitrateChanged: (Float) -> Unit = {},
    val onQuickSettingsTouchscreenTrackpadChanged: (Boolean) -> Unit = {},
    val onQuickSettingsOnscreenControllerChanged: (Boolean) -> Unit = {},
    val onQuickSettingsHostAudioChanged: (Boolean) -> Unit = {},
    val onQuickSettingsMouseEmulationChanged: (Boolean) -> Unit = {},
    val onQuickSettingsVibrateOscChanged: (Boolean) -> Unit = {},
    val onQuickSettingsDismiss: () -> Unit = {},
    // Connection
    val onConnectionInitiate: (String) -> Unit = {},
    val onConnectionCancel: () -> Unit = {},
    val onLaunchApp: (NvApp, String) -> Unit = { _, _ -> },
    // App Item
    val onAppMenuOpen: (Int, String) -> Unit = { _, _ -> },
    val onAppMenuDismiss: () -> Unit = {},
    val onAppQuit: (NvApp, String) -> Unit = { _, _ -> },
    val onAppDetailsClick: (NvApp) -> Unit = {},
    val onAppDetailsDismiss: () -> Unit = {},
    val onAppMoveUp: (String, Int) -> Unit = { _, _ -> },
    val onAppMoveDown: (String, Int) -> Unit = { _, _ -> },
    // Computer Item
    val onComputerMenuOpen: (String) -> Unit = {},
    val onComputerMenuDismiss: () -> Unit = {},
    val onComputerDetailsClick: (Computer) -> Unit = {},
    val onComputerDetailsDismiss: () -> Unit = {},
    val onComputerQuitRunningApp: (Computer) -> Unit = {},
    val onComputerWakeOnLan: (String) -> Unit = {},
    val onComputerMoveUp: (String) -> Unit = {},
    val onComputerMoveDown: (String) -> Unit = {},
    val onComputerDelete: (Computer) -> Unit = {},
    val onComputerTestNetwork: () -> Unit = {},
    val onComputerDismissNetworkTest: () -> Unit = {},
    // Confirmation
    val onConfirmationConfirm: () -> Unit = {},
    val onConfirmationDismiss: () -> Unit = {},
)

/**
 * Stateless version of the Main Screen.
 * Renders based on the provided state and triggers events via lambdas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    state: MainScreenUiState,
    actions: MainScreenActions,
) {
    val context = LocalContext.current
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
        if (state.computers.isEmpty()) {
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
                items(state.computers, key = { it.details.uuid }) { computer ->
                    val assetLoader = remember(computer.details, state.uniqueId) {
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
                            NetworkAssetLoader(context, state.uniqueId ?: ""),
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
                                uiState = computer.toUiState(),
                                computer = computer,
                                isMenuExpanded = state.computerMenuUiState.computerUuid == computer.details.uuid,
                                onDismissMenu = actions.onComputerMenuDismiss,
                                onSendWakeOnLan = { actions.onComputerWakeOnLan(computer.details.uuid) },
                                onQuitRunningApp = { actions.onComputerQuitRunningApp(computer) },
                                onComputerDetailsClicked = { actions.onComputerDetailsClick(computer) },
                                onMoveUp = { actions.onComputerMoveUp(computer.details.uuid) },
                                onMoveDown = { actions.onComputerMoveDown(computer.details.uuid) },
                                onTestNetwork = actions.onComputerTestNetwork,
                                onClick = { actions.onConnectionInitiate(computer.details.uuid) },
                                onLongClick = { actions.onComputerMenuOpen(computer.details.uuid) },
                                canMoveUp = computer != state.computers.first(),
                                canMoveDown = computer != state.computers.last(),
                                onDeleteComputer = { actions.onComputerDelete(computer) },
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(16f / 9f)
                                    .then(if (state.computers.first() == computer)
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
                                    isMenuExpanded = state.appMenuUiState.appId == app.appId &&
                                            state.appMenuUiState.computerUuid == computer.details.uuid,
                                    onDismissMenu = actions.onAppMenuDismiss,
                                    onQuitApp = { actions.onAppQuit(app, computer.details.uuid) },
                                    onAppDetailsClicked = { actions.onAppDetailsClick(app) },
                                    onMoveLeft = { actions.onAppMoveUp(computer.details.uuid, app.appId) },
                                    onMoveRight = { actions.onAppMoveDown(computer.details.uuid, app.appId) },
                                    onClick = { actions.onLaunchApp(app, computer.details.uuid) },
                                    onLongClick = { actions.onAppMenuOpen(app.appId, computer.details.uuid) },
                                    canMoveLeft = app != computer.apps.first(),
                                    canMoveRight = app != computer.apps.last(),
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(2f / 3f)
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(state.computers) {
                if (state.computers.isNotEmpty()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
//    }

    MainScreenDialogs(
        state = state,
        actions = actions
    )
}

/**
 * Stateful version of the Main Screen.
 * Collects state from the ViewModel and passes it to the stateless MainScreenContent.
 */
@Composable
fun MainScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val computers by viewModel.computers.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val uniqueId by viewModel.uniqueId.collectAsStateWithLifecycle()
    val networkTestStatus by viewModel.networkTestStatus.collectAsStateWithLifecycle()

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
        state = MainScreenUiState(
            computers = computers,
            isRefreshing = isRefreshing,
            uniqueId = uniqueId,
            networkTestStatus = networkTestStatus,
            manualComputerAddUiState = viewModel.manualComputerAddHandler.uiState,
            connectionUiState = viewModel.connectionHandler.uiState,
            appMenuUiState = viewModel.appItemHandler.uiState,
            appViewDetailsUiState = viewModel.appItemHandler.viewDetails,
            computerMenuUiState = viewModel.computerItemHandler.uiState,
            computerViewDetailsUiState = viewModel.computerItemHandler.viewDetails,
            networkTestUiState = viewModel.computerItemHandler.networkTest,
            confirmationUiState = viewModel.confirmationHandler.uiState,
            quickSettingsUiState = viewModel.quickSettingsHandler.uiState
        ),
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
                        state = MainScreenUiState(
                            computers = computers,
                        ),
                        actions = MainScreenActions()
                    )
                }
            }
        }
    }
}

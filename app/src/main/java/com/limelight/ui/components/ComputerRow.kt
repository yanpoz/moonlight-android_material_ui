package com.limelight.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.computers.toUiState
import com.limelight.grid.assets.CachedAppAssetLoader
import com.limelight.grid.assets.DiskAssetLoader
import com.limelight.grid.assets.MemoryAssetLoader
import com.limelight.grid.assets.NetworkAssetLoader
import com.limelight.nvstream.http.PairingManager
import com.limelight.ui.components.cards.AppItemCard
import com.limelight.ui.components.cards.ComputerItemCard
import com.limelight.viewmodel.MainScreenActions
import com.limelight.viewmodel.components.AppMenuUiState
import com.limelight.viewmodel.components.ComputerMenuUiState

@Composable
fun ComputerRow(
    computer: Computer,
    uniqueId: String?,
    computerMenuUiState: ComputerMenuUiState,
    appMenuUiState: AppMenuUiState,
    actions: MainScreenActions,
    isFirstComputer: Boolean,
    isLastComputer: Boolean,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
        modifier = modifier
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
                details = computer.details,
                isMenuExpanded = computerMenuUiState.computerUuid == computer.details.uuid,
                onDismissMenu = actions.onComputerMenuDismiss,
                onSendWakeOnLan = { actions.onComputerWakeOnLan(computer.details.uuid) },
                onQuitRunningApp = { actions.onComputerQuitRunningApp(computer) },
                onComputerDetailsClicked = { actions.onComputerDetailsClick(computer) },
                onMoveUp = { actions.onComputerMoveUp(computer.details.uuid) },
                onMoveDown = { actions.onComputerMoveDown(computer.details.uuid) },
                onTestNetwork = actions.onComputerTestNetwork,
                onClick = { actions.onConnectionInitiate(computer.details.uuid) },
                onLongClick = { actions.onComputerMenuOpen(computer.details.uuid) },
                canMoveUp = !isFirstComputer,
                canMoveDown = !isLastComputer,
                onDeleteComputer = { actions.onComputerDelete(computer) },
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(16f / 9f),
                focusRequester = if (isFirstComputer) focusRequester else null
            )
        }
        // AppItems
        if (computer.details.pairState == PairingManager.PairState.PAIRED) {
            items(computer.apps, key = { it.appId }) { app ->
                AppItemCard(
                    app = app,
                    assetLoader = assetLoader,
                    runningGameId = computer.details.runningGameId,
                    isMenuExpanded = appMenuUiState.appId == app.appId &&
                            appMenuUiState.computerUuid == computer.details.uuid,
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

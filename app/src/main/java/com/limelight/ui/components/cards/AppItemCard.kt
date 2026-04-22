package com.limelight.ui.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import com.limelight.grid.assets.CachedAppAssetLoader
import com.limelight.nvstream.http.NvApp
import com.limelight.ui.components.AppImage
import com.limelight.ui.components.menus.AppItemMenu
import com.limelight.ui.theme.MoonlightAndroidTheme
import com.limelight.viewmodel.components.StatusLabelUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItemCard(
    app: NvApp,
    assetLoader: CachedAppAssetLoader?,
    isMenuExpanded: Boolean,
    runningGameId: Int,
    onDismissMenu: () -> Unit,
    onQuitApp: () -> Unit,
    onAppDetailsClicked: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    canMoveLeft: Boolean,
    canMoveRight: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    statusLabel: StatusLabelUiState? = null
) {
    ItemCard(
        onClick = onClick,
        onLongClick = onLongClick,
        statusLabel = statusLabel,
        modifier = modifier
            .aspectRatio(2f / 3f) // Vertical card (3:2 height:width)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (assetLoader != null) {
                AppImage(
                    app = app,
                    assetLoader = assetLoader,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        AppItemMenu(
            app = app,
            isExpanded = isMenuExpanded,
            onDismissRequest = onDismissMenu,
            runningGameId = runningGameId,
            onQuitApp = onQuitApp,
            onAppDetailsClicked = onAppDetailsClicked,
            onMoveLeft = onMoveLeft,
            onMoveRight = onMoveRight,
            canMoveLeft = canMoveLeft,
            canMoveRight = canMoveRight,
            onClick = onClick
        )
    }
}

@Preview
@Composable
fun AppItemCardPreview() {
    val app = NvApp()
    MoonlightAndroidTheme {
        AppItemCard(
            app = app,
            assetLoader = null,
            isMenuExpanded = false,
            runningGameId = 0,
            onDismissMenu = {},
            onQuitApp = {},
            onAppDetailsClicked = {},
            onMoveLeft = {},
            onMoveRight = {},
            canMoveLeft = true,
            canMoveRight = true,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview
@Composable
fun AppItemCardLivePreview() {
    val app = NvApp()
    MoonlightAndroidTheme {
        AppItemCard(
            app = app,
            assetLoader = null,
            isMenuExpanded = false,
            runningGameId = 0,
            onDismissMenu = {},
            onQuitApp = {},
            onAppDetailsClicked = {},
            onMoveLeft = {},
            onMoveRight = {},
            canMoveLeft = true,
            canMoveRight = true,
            onClick = {},
            onLongClick = {},
            statusLabel = StatusLabelUiState(
                text = "Live",
                textColor = MaterialTheme.colorScheme.onTertiary,
                statusColor = MaterialTheme.colorScheme.tertiary
            )
        )
    }
}

package com.limelight.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.grid.assets.CachedAppAssetLoader
import com.limelight.nvstream.http.NvApp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(
    app: NvApp,
    assetLoader: CachedAppAssetLoader?,
    isMenuExpanded: Boolean,
    runningGameId: Int,
    onDismissMenu: () -> Unit,
    onQuitApp: () -> Unit,
    onAppDetailsClicked: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(2f / 3f) // Vertical card (3:2 height:width)
            .clip(CardDefaults.shape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .onKeyEvent {
                if (it.key == Key.DirectionCenter) {
                    onLongClick()
                    return@onKeyEvent true
                }
                false
            }
            .focusable()
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

        DropdownMenu(
            modifier = Modifier.widthIn(min = 220.dp),
            expanded = isMenuExpanded,
            onDismissRequest = onDismissMenu
        ) {
            if (runningGameId != 0) {
                if (runningGameId == app.appId) {
                    // Resume Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_resume)) },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = {
                            onDismissMenu()
                            onClick()
                        }
                    )
                    // Quit Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit)) },
                        leadingIcon = { Icon(Icons.Outlined.Close, null) },
                        onClick = {
                            onDismissMenu()
                            onQuitApp()
                        }
                    )
                    HorizontalDivider() // TODO: replace with gap Material expressive
                } else {
                    // Quit running and Start new session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit_and_start)) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null) },
                        onClick = { onDismissMenu() /*TODO*/ }
                    )
                    HorizontalDivider() // TODO: replace with gap Material expressive
                }
            }
            // Move Left TODO: should not be available when on beginning
            DropdownMenuItem(
                text = { Text(text = "Move Left") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, null) },
                onClick = { onDismissMenu() /*TODO*/ }
            )
            // Move Right TODO: should not be available when on bottom
            DropdownMenuItem(
                text = { Text(text = "Move Right") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) },
                onClick = { onDismissMenu() /*TODO*/ }
            )
            HorizontalDivider() // TODO: replace with gap Material expressive
            // App Details
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_details)) },
                // leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = {
                    onDismissMenu()
                    onAppDetailsClicked()
                }
            )
            // Create shortcut
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_scut)) },
                // leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = { onDismissMenu() /*TODO*/ }
            )
            // Hide App
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_hide_app)) },
                // leadingIcon = { Icon(Icons.Outlined.VisibilityOff, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = { onDismissMenu() /*TODO*/ }
            )
        }
    }
}

@Preview
@Composable
fun AppItemPreview() {
    val app = NvApp(
//        appName = "Steam",
//        appId = 123
    )
    AppItem(
        app = app,
        assetLoader = null,
        isMenuExpanded = false,
        runningGameId = 0,
        onDismissMenu = {},
        onQuitApp = {},
        onAppDetailsClicked = {},
        onClick = {},
        onLongClick = {}
    )
}

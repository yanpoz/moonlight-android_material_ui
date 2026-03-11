package com.limelight.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.grid.assets.CachedAppAssetLoader
import com.limelight.nvstream.http.NvApp

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
    modifier: Modifier = Modifier
) {
    ItemCard(
        onClick = onClick,
        onLongClick = onLongClick,
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

        // TODO: replace with expressive menu, implement missing items
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
                    HorizontalDivider()
                } else {
                    // Quit running and Start new session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit_and_start)) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null) },
                        onClick = { onDismissMenu() }
                    )
                    HorizontalDivider()
                }
            }
            // Move Left
            DropdownMenuItem(
                text = { Text(text = "Move Left") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, null) },
                onClick = {
                    onDismissMenu()
                    onMoveLeft()
                },
                enabled = canMoveLeft
            )
            // Move Right
            DropdownMenuItem(
                text = { Text(text = "Move Right") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) },
                onClick = {
                    onDismissMenu()
                    onMoveRight()
                },
                enabled = canMoveRight
            )
            HorizontalDivider()
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
                onClick = { onDismissMenu() }
            )
            // Hide App
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_hide_app)) },
                // leadingIcon = { Icon(Icons.Outlined.VisibilityOff, null) },
                leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
                onClick = { onDismissMenu() }
            )
        }
    }
}

@Preview
@Composable
fun AppItemCardPreview() {
    val app = NvApp()
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

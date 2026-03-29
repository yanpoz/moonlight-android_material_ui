package com.limelight.ui.components.menus

import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.nvstream.http.NvApp

@Composable
fun AppItemMenu(
    app: NvApp,
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    runningGameId: Int,
    onQuitApp: () -> Unit,
    onAppDetailsClicked: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    canMoveLeft: Boolean,
    canMoveRight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier.widthIn(min = 220.dp),
        expanded = isExpanded,
        onDismissRequest = onDismissRequest
    ) {
        if (runningGameId != 0) {
            if (runningGameId == app.appId) {
                // Resume Session
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.applist_menu_resume)) },
                    leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                    onClick = {
                        onDismissRequest()
                        onClick()
                    }
                )
                // Quit Session
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.applist_menu_quit)) },
                    leadingIcon = { Icon(Icons.Outlined.Close, null) },
                    onClick = {
                        onDismissRequest()
                        onQuitApp()
                    }
                )
                HorizontalDivider()
            } else {
                // Quit running and Start new session
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.applist_menu_quit_and_start)) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null) },
                    onClick = { onDismissRequest() }
                )
                HorizontalDivider()
            }
        }
        // Move Left
        DropdownMenuItem(
            text = { Text(text = "Move Left") },
            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, null) },
            onClick = {
                onDismissRequest()
                onMoveLeft()
            },
            enabled = canMoveLeft
        )
        // Move Right
        DropdownMenuItem(
            text = { Text(text = "Move Right") },
            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) },
            onClick = {
                onDismissRequest()
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
                onDismissRequest()
                onAppDetailsClicked()
            }
        )
        // Create shortcut
        DropdownMenuItem(
            text = { Text(stringResource(R.string.applist_menu_scut)) },
            // leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
            leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
            onClick = { onDismissRequest() }
        )
        // Hide App
        DropdownMenuItem(
            text = { Text(stringResource(R.string.applist_menu_hide_app)) },
            // leadingIcon = { Icon(Icons.Outlined.VisibilityOff, null) },
            leadingIcon = { Spacer(modifier = Modifier.size(24.dp)) },
            onClick = { onDismissRequest() }
        )
    }
}

package com.limelight.ui.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.limelight.R
import com.limelight.computers.Computer
import com.limelight.nvstream.http.NvApp
import com.limelight.viewmodel.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(
    viewModel: MainViewModel, app: NvApp, computer: Computer, context: Context,
    onClick: () -> Unit, modifier: Modifier = Modifier.Companion
) {
    Card(
        modifier = modifier
            .aspectRatio(2f / 3f) // Vertical card (3:2 height:width)
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    viewModel.onAppLongClick(app.appId, computer.details.uuid)
                }
            )
    ) {
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom // Align app name to the bottom
        ) {
            Text(text = app.appName, style = MaterialTheme.typography.bodyLarge)
        }

        DropdownMenu(
            modifier = Modifier.Companion.widthIn(min = 220.dp),
            expanded = viewModel.appMenu.appId == app.appId &&
                    viewModel.appMenu.computerUuid == computer.details.uuid,
            onDismissRequest = { viewModel.dismissAppMenu() }
        ) {
            if (computer.details.runningGameId != 0) {
                if (computer.details.runningGameId == app.appId) {
                    // Resume Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_resume)) },
                        leadingIcon = { Icon(Icons.Outlined.PlayArrow, null) },
                        onClick = { viewModel.dismissAppMenu() /*TODO*/ }
                    )
                    // Quit Session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit)) },
                        leadingIcon = { Icon(Icons.Outlined.Close, null) },
                        onClick = {
                            viewModel.dismissAppMenu()
                            viewModel.onQuitApp(context, app, computer.details.uuid)
                        }
                    )
                    HorizontalDivider() // TODO: replace with gap Material expressive
                } else {
                    // Quit running and Start new session
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.applist_menu_quit_and_start)) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, null) },
                        onClick = { viewModel.dismissAppMenu() /*TODO*/ }
                    )
                    HorizontalDivider() // TODO: replace with gap Material expressive
                }
            }
            // Move Left TODO: should not be available when on beginning
            DropdownMenuItem(
                text = { Text(text = "Move Left") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            // Move Right TODO: should not be available when on bottom
            DropdownMenuItem(
                text = { Text(text = "Move Right") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null) },
                onClick = { viewModel.dismissComputerMenu() /*TODO*/ }
            )
            HorizontalDivider() // TODO: replace with gap Material expressive
            // App Details
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_details)) },
                // leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, null) },
                leadingIcon = { Spacer(modifier = Modifier.Companion.size(24.dp)) },
                onClick = {
                    viewModel.dismissAppMenu()
                    viewModel.onAppDetailsClicked(app, computer)
                }
            )
            // Create shortcut
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_scut)) },
                // leadingIcon = { Icon(Icons.Outlined.StarOutline, null) },
                leadingIcon = { Spacer(modifier = Modifier.Companion.size(24.dp)) },
                onClick = { viewModel.dismissAppMenu() /*TODO*/ }
            )
            // Hide App
            DropdownMenuItem(
                text = { Text(stringResource(R.string.applist_menu_hide_app)) },
                // leadingIcon = { Icon(Icons.Outlined.VisibilityOff, null) },
                leadingIcon = { Spacer(modifier = Modifier.Companion.size(24.dp)) },
                onClick = { viewModel.dismissAppMenu() /*TODO*/ }
            )
        }
    }
}
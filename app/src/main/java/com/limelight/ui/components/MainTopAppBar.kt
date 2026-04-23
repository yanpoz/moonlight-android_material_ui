package com.limelight.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.limelight.R
import com.limelight.ui.theme.MoonlightAndroidTheme
import com.limelight.ui.theme.SixtyfourTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onShowManualAddDialog: () -> Unit,
    onShowQuickSettings: () -> Unit,
    onHelpClick: () -> Unit,
    onSettingsClick: () -> Unit,
    navigationIconFocusRequester: FocusRequester = remember { FocusRequester() }
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction

    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = "MOONLIGHT",
                style = SixtyfourTextStyle.copy(
                    // Interpolate font size between expanded (32.sp) and collapsed (22.sp)
                    // This ensures the title fits within the smaller collapsed bar height.
                    fontSize = lerp(SixtyfourTextStyle.fontSize, 22.sp, collapsedFraction),
                    fontWeight =  MaterialTheme.typography.titleLarge.fontWeight
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    // Adjust vertical alignment in collapsed state by adding top padding
                    // that increases as the app bar collapses.
                    .padding(top = (6 * collapsedFraction).dp)
            )
        },
        navigationIcon = {
            FocusedIconButton(
                onClick = onShowManualAddDialog,
                modifier = Modifier.focusRequester(navigationIconFocusRequester)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = stringResource(R.string.title_add_pc)
                )
            }
        },
        actions = {
            FocusedIconButton(onClick = onShowQuickSettings) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Quick Settings"
                )
            }
            FocusedIconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.help)
                )
            }
            FocusedIconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        },
    )
}

@Composable
private fun FocusedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    IconButton(
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor =
                if (isFocused)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else Color.Transparent,
            contentColor =
                if (isFocused)
                    MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.onSurface
        )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainTopAppBarPreview() {
    MoonlightAndroidTheme {
        MainTopAppBar(
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            onShowManualAddDialog = {},
            onShowQuickSettings = {},
            onHelpClick = {},
            onSettingsClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainTopAppBarCollapsedPreview() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(initialHeightOffset = -50f)
    )
    MoonlightAndroidTheme {
        MainTopAppBar(
            scrollBehavior = scrollBehavior,
            onShowManualAddDialog = {},
            onShowQuickSettings = {},
            onHelpClick = {},
            onSettingsClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainTopAppBarFocusedPreview() {
    val focusRequester = remember { FocusRequester() }
    MoonlightAndroidTheme {
        MainTopAppBar(
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            onShowManualAddDialog = {},
            onShowQuickSettings = {},
            onHelpClick = {},
            onSettingsClick = {},
            navigationIconFocusRequester = focusRequester
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

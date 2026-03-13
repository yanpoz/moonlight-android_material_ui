package com.limelight.ui

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limelight.R
import com.limelight.repository.SettingCategory
import com.limelight.repository.SettingItem
import com.limelight.ui.components.settings.ActionSettingListItem
import com.limelight.ui.components.settings.SelectionDialog
import com.limelight.ui.components.settings.SelectionSettingListItem
import com.limelight.ui.components.settings.SliderDialog
import com.limelight.ui.components.settings.SliderSettingListItem
import com.limelight.ui.components.settings.ToggleSettingListItem
import com.limelight.viewmodel.SettingsUiState
import com.limelight.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsScreenContent(
        uiState = uiState,
        onCategorySelected = { viewModel.selectCategory(it) },
        onSettingToggled = { categoryName, settingName, isEnabled ->
            viewModel.onSettingToggled(categoryName, settingName, isEnabled)
        },
        onSettingSelected = { categoryName, settingName, newValue ->
            viewModel.onSettingSelected(categoryName, settingName, newValue)
        },
        onSelectionItemClick = { viewModel.showSelectionDialog(it) },
        onSliderItemClick = { viewModel.showSliderDialog(it) },
        onDismissSelectionDialog = { viewModel.dismissSelectionDialog() },
        onDismissSliderDialog = { viewModel.dismissSliderDialog() },
        onSliderValueChanged = { categoryName, settingName, newValue ->
            viewModel.onSliderValueChanged(categoryName, settingName, newValue)
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onCategorySelected: (SettingCategory) -> Unit,
    onSettingToggled: (String, String, Boolean) -> Unit,
    onSettingSelected: (String, String, String) -> Unit,
    onSelectionItemClick: (SettingItem.Selection) -> Unit,
    onSliderItemClick: (SettingItem.Slider) -> Unit,
    onDismissSelectionDialog: () -> Unit,
    onDismissSliderDialog: () -> Unit,
    onSliderValueChanged: (String, String, Float) -> Unit,
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<SettingCategoryItem>()
    val scope = rememberCoroutineScope()

    // During screen opening, select first category if detailPane is visible (on large screens)
    LaunchedEffect(scaffoldNavigator.scaffoldValue, uiState.categories) {
        if (uiState.selectedCategory == null &&
            uiState.categories.isNotEmpty() &&
            scaffoldNavigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded
        ) {
            onCategorySelected(uiState.categories.first())
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                SettingsCategoryList(
                    categories = uiState.categories,
                    onCategoryClick = { category ->
                        onCategorySelected(category)
                        scope.launch {
                            scaffoldNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                SettingCategoryItem(category.name)
                            )
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                uiState.selectedCategory?.let { category ->
                    SettingsCategoryDetail(
                        category = category,
                        onSettingToggled = { settingName, isEnabled ->
                            onSettingToggled(category.name, settingName, isEnabled)
                        },
                        onSelectionItemClick = { item ->
                            onSelectionItemClick(item)
                        },
                        onSliderItemClick = { item ->
                            onSliderItemClick(item)
                        }
                    )
                }
            }
        }
    )

    uiState.openSelectionDialog?.let { item ->
        SelectionDialog(
            item = item,
            onDismiss = { onDismissSelectionDialog() },
            onSelected = { newValue ->
                onSettingSelected(item.category, item.name, newValue)
            }
        )
    }

    uiState.openSliderDialog?.let { item ->
        SliderDialog(
            item = item,
            onDismiss = { onDismissSliderDialog() },
            onValueChange = { newValue ->
                onSliderValueChanged(item.category, item.name, newValue)
            }
        )
    }
}

@Parcelize
data class SettingCategoryItem(val name: String) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCategoryList(
    categories: List<SettingCategory>,
    onCategoryClick: (SettingCategory) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(categories.size) { index ->
                val category = categories[index]
                ListItem(
                    leadingContent = {
                        Icon(imageVector = category.icon, contentDescription = null)
                    },
                    headlineContent = {
                        Text(stringResource(category.categoryTitle))
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onCategoryClick(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCategoryDetail(
    category: SettingCategory,
    onSettingToggled: (String, Boolean) -> Unit,
    onSelectionItemClick: (SettingItem.Selection) -> Unit,
    onSliderItemClick: (SettingItem.Slider) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(category.categoryTitle)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(category.items.size) { index ->
                when (val item = category.items[index]) {
                    is SettingItem.Toggle -> {
                        ToggleSettingListItem(item) {
                            onSettingToggled(item.name, it)
                        }
                    }
                    is SettingItem.Selection -> {
                        SelectionSettingListItem(item) {
                            onSelectionItemClick(item)
                        }
                    }
                    is SettingItem.Slider -> {
                        SliderSettingListItem(item) {
                            onSliderItemClick(item)
                        }
                    }
                    is SettingItem.Action -> {
                        ActionSettingListItem(item)
                    }
                }
                if (index < category.items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
fun SettingsScreenPreview() {
    val mockCategories = listOf(
        SettingCategory(
            categoryTitle = R.string.category_basic_settings,
            name = "Video",
            icon = Icons.Outlined.Theaters,
            items = listOf(
                SettingItem.Toggle(
                    name = "Stretch",
                    category = "Video",
                    title = R.string.title_checkbox_stretch_video,
                    summary = R.string.title_checkbox_stretch_video,
                    default = true,
                    onToggle = {}
                ),
                SettingItem.Selection(
                    name = "Resolution",
                    category = "Video",
                    title = R.string.title_resolution_list,
                    summary = R.string.summary_resolution_list,
                    entries = listOf("1080p", "720p"),
                    entryValues = listOf("1080", "720"),
                    currentValue = "1080",
                    onSelected = {}
                )
            )
        ),
        SettingCategory(
            categoryTitle = R.string.category_audio_settings,
            name = "Audio",
            icon = Icons.AutoMirrored.Outlined.VolumeUp,
            items = emptyList()
        )
    )

    SettingsScreenContent(
        uiState = SettingsUiState(
            categories = mockCategories,
            selectedCategory = mockCategories.first()
        ),
        onCategorySelected = {},
        onSettingToggled = { _, _, _ -> },
        onSettingSelected = { _, _, _ -> },
        onSelectionItemClick = {},
        onSliderItemClick = {},
        onDismissSelectionDialog = {},
        onDismissSliderDialog = {},
        onSliderValueChanged = { _, _, _ -> }
    )
}

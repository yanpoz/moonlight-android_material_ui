package com.limelight.ui

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limelight.repository.SettingCategory
import com.limelight.repository.SettingItem
import com.limelight.ui.components.settings.ActionSettingListItem
import com.limelight.ui.components.settings.SelectionDialog
import com.limelight.ui.components.settings.SelectionSettingListItem
import com.limelight.ui.components.settings.ToggleSettingListItem
import com.limelight.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview(widthDp = 840, heightDp = 800, showBackground = true)
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    // During screen opening, select first category if detailPane is visible (on large screens)
    LaunchedEffect(scaffoldNavigator.scaffoldValue, uiState.categories) {
        if (uiState.selectedCategory == null && uiState.categories.isNotEmpty())
        {
            viewModel.selectCategory(uiState.categories.first())
            scaffoldNavigator.navigateTo(
                ListDetailPaneScaffoldRole.Detail,
                SettingCategoryItem(uiState.categories.first().name)
            )
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                SettingsCategoryList(
                    categories = uiState.categories,
                    onCategoryClick = { category ->
                        viewModel.selectCategory(category)
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
                            viewModel.onSettingToggled(category.name, settingName, isEnabled)
                        },
                        onSettingSelected = { settingName, newValue ->
                            viewModel.onSettingSelected(category.name, settingName, newValue)
                        },
                        onSelectionItemClick = { item ->
                            viewModel.showSelectionDialog(item)
                        }
                    )
                }
            }
        }
    )

    uiState.openSelectionDialog?.let { item ->
        SelectionDialog(
            item = item,
            onDismiss = { viewModel.dismissSelectionDialog() },
            onSelected = { newValue ->
                viewModel.onSettingSelected(item.category, item.name, newValue)
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
    onSettingSelected: (String, String) -> Unit,
    onSelectionItemClick: (SettingItem.Selection) -> Unit
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
                    is SettingItem.Action -> {
                        ActionSettingListItem(item)
                    }
                }
            }
        }
    }
}

package com.limelight.ui

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.limelight.repository.SettingCategory
import com.limelight.repository.SettingItem
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
                if (index < category.items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}


@Composable
fun ToggleSettingListItem(settingItem: SettingItem.Toggle, onToggle: (Boolean) -> Unit) {
    ListItem(
        modifier = Modifier
            .toggleable(
                value = settingItem.default,
                onValueChange = onToggle,
                role = Role.Switch
            )
            .height(IntrinsicSize.Min),
        headlineContent = {
            Text(stringResource(settingItem.title))
        },
        supportingContent = {
            Text(stringResource(settingItem.summary))
        },
        trailingContent = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Switch(
                    checked = settingItem.default,
                    onCheckedChange = null
                )
            }
        }
    )
}

@Composable
fun SelectionSettingListItem(item: SettingItem.Selection, onClick: () -> Unit) {
    val currentEntryLabel = item.entries.getOrNull(item.entryValues.indexOf(item.currentValue)) ?: item.currentValue

    ListItem(
        modifier = Modifier
            .clickable { onClick() }
            .height(IntrinsicSize.Min),
        headlineContent = {
            Text(stringResource(item.title))
        },
        supportingContent = {
            Text(stringResource(item.summary))
        },
        trailingContent = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(currentEntryLabel)
            }
        }
    )
}

@Composable
fun SelectionDialog(
    item: SettingItem.Selection,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    AlertDialog(
        modifier = Modifier.widthIn(min = 400.dp),
        onDismissRequest = onDismiss,
        title = { Text(stringResource(item.title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = stringResource(item.summary),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                item.entries.forEachIndexed { index, entry ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (item.entryValues[index] == item.currentValue),
                                onClick = {
                                    onSelected(item.entryValues[index])
                                },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (item.entryValues[index] == item.currentValue),
                            onClick = null
                        )
                        Text(
                            text = entry,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ActionSettingListItem(item: SettingItem.Action) {
    ListItem(
        modifier = Modifier.clickable { item.onClick() },
        headlineContent = {
            Text(stringResource(item.title))
        },
        supportingContent = {
            Text(stringResource(item.summary))
        }
    )
}

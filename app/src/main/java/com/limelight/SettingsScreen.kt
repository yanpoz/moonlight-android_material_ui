package com.limelight

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.limelight.viewmodel.SettingCategory
import com.limelight.viewmodel.SettingItem
import com.limelight.viewmodel.SettingsData
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun SettingsScreen() {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<SettingCategoryItem>()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane {
                SettingsCategoryList(
                    categories = SettingsData.categories,
                    onCategoryClick = { category ->
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
                scaffoldNavigator.currentDestination?.contentKey?.let { categoryItem ->
                    // Find the corresponding category
                    val selectedCategory = SettingsData.categories.find { it.name == categoryItem.name }
                    if (selectedCategory != null) {
                        SettingsCategoryDetail(selectedCategory)
                    }
                }
            }
        }
    )
}

@Parcelize
data class SettingCategoryItem(val name: String) : Parcelable

@Composable
fun SettingsCategoryList(
    categories: List<SettingCategory>,
    onCategoryClick: (SettingCategory) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp).background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(categories.size) { index ->
                val category = categories[index]
                ListItem(
                    leadingContent = {
                        Icon(imageVector = category.icon, contentDescription = null)
                    },
                    headlineContent = {
                        Text(stringResource(category.headline))
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

@Composable
fun SettingsCategoryDetail(category: SettingCategory) {
    Card(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = category.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (category.items.isEmpty()) {
                Text("No settings available in this category")
            } else {
                LazyColumn {
                    items(category.items.size) { index ->
                        when (val item = category.items[index]) {
                            is SettingItem.Toggle -> ToggleSettingItem(item)
                            is SettingItem.Slider -> SliderSettingItem(item)
                        }

                        if (index < category.items.size - 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleSettingItem(item: SettingItem.Toggle) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Medium
            )
        }

        Switch(
            checked = item.isEnabled,
            onCheckedChange = item.onToggle
        )
    }
}

@Composable
fun SliderSettingItem(item: SettingItem.Slider) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = item.name,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(text = item.range.start.toString())

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
                value = item.value,
                onValueChange = item.onValueChange,
                valueRange = item.range,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = item.range.endInclusive.toString())
        }
    }
}
package com.limelight.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

// Represents a single setting item
sealed class SettingItem {
    abstract val title: String
    abstract val category: String

    data class Toggle(
        override val title: String,
        override val category: String,
        val isEnabled: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItem()

    data class Slider(
        override val title: String,
        override val category: String,
        val value: Float,
        val range: ClosedFloatingPointRange<Float>,
        val onValueChange: (Float) -> Unit
    ) : SettingItem()

    // Add more types as needed (e.g., TextInput, Dropdown)
}

// Represents a category with its settings and icon
data class SettingCategory(
    val name: String,
    val icon: ImageVector = Icons.Default.Build,
    val items: List<SettingItem>,
)

// Centralized settings data source
object SettingsData {
    val categories = listOf(
        SettingCategory(
            name = "Video",
            icon = Icons.Default.Face,
            items = listOf(
                SettingItem.Toggle("Enable Fullscreen", "Video", true) { /* Handle toggle */ },
                SettingItem.Slider("Brightness", "Video", 0.5f, 0f..1f) { /* Handle slider */ }
            ),
        ),
        SettingCategory(
            name = "Audio",
            icon = Icons.Default.Notifications,
            items = listOf(
                SettingItem.Toggle("Mute", "Audio", false) { /* Handle toggle */ },
                SettingItem.Slider("Volume", "Audio", 0.8f, 0f..1f) { /* Handle slider */ }
            ),
        ),
        SettingCategory(
            name = "Gamepad",
            icon = Icons.Default.Person,
            items = emptyList(),
        ),
        SettingCategory(
            name = "Mouse",
            icon = Icons.Default.AccountBox,
            items = emptyList(),
        ),
        SettingCategory(
            name = "Virtual Pad",
            icon = Icons.Default.AccountCircle,
            items = emptyList(),
        ),
        SettingCategory(
            name = "Host",
            icon = Icons.Default.Home,
            items = emptyList(),
        ),
        SettingCategory(
            name = "Appearance",
            icon = Icons.Default.Star,
            items = listOf(
                SettingItem.Toggle("Dark Mode", "Appearance", false) { /* Handle toggle */ }
            ),
        ),
        SettingCategory(
            name = "Advanced",
            Icons.Default.Build,
            items = emptyList(),
        )
    )
}
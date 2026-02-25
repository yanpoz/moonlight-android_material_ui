package com.limelight.repository

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Mouse
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.ui.graphics.vector.ImageVector
import com.limelight.R

// Represents a single setting item
sealed class SettingItem {
    abstract val name: String
    abstract val category: String
    @get:StringRes abstract val title: Int
    @get:StringRes abstract val summary: Int

    data class Toggle(
        override val name: String,
        override val category: String,
        override val title: Int,
        override val summary: Int,
        val isEnabled: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItem()

    // Add more types as needed (e.g., TextInput, Dropdown)
}

// Represents a category with its settings and icon
data class SettingCategory(
    @StringRes val categoryTitle: Int,
    val name: String,
    val icon: ImageVector = Icons.Default.Build,
    val items: List<SettingItem>,
)

// Centralized settings data source
object SettingsData {
    val categories = listOf(
        SettingCategory(
            name = "Video",
            categoryTitle = R.string.category_basic_settings,
            icon = Icons.Outlined.Theaters,
            items = listOf(
                SettingItem.Toggle(
                    name = "Enable Fullscreen",
                    category = "Video",
                    title = R.string.title_checkbox_stretch_video,
                    summary = R.string.summary_resolution_list,
                    isEnabled = true
                ) { /* Handle toggle */ }
            ),
        ),
        SettingCategory(
            name = "Audio",
            categoryTitle = R.string.category_audio_settings,
            icon = Icons.AutoMirrored.Outlined.VolumeUp,
            items = listOf(
                SettingItem.Toggle(
                    name = "Mute",
                    category = "Audio",
                    title = R.string.title_checkbox_enable_audiofx,
                    summary = R.string.summary_checkbox_enable_audiofx,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Gamepad",
            categoryTitle = R.string.category_gamepad_settings,
            icon = Icons.Outlined.SportsEsports,
            items = listOf(
                SettingItem.Toggle(
                    name = "Automatic gamepad presence detection",
                    category = "Gamepad",
                    title = R.string.title_checkbox_multi_controller,
                    summary = R.string.summary_checkbox_multi_controller,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Xbox 360/One USB gamepad driver",
                    category = "Gamepad",
                    title = R.string.title_checkbox_xb1_driver,
                    summary = R.string.summary_checkbox_xb1_driver,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Override native Xbox gamepad support",
                    category = "Gamepad",
                    title = R.string.title_checkbox_usb_bind_all,
                    summary = R.string.summary_checkbox_usb_bind_all,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Mouse emulation via gamepad",
                    category = "Gamepad",
                    title = R.string.title_checkbox_mouse_emulation,
                    summary = R.string.summary_checkbox_mouse_emulation,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Emulate rumble support with vibration",
                    category = "Gamepad",
                    title = R.string.title_checkbox_vibrate_fallback,
                    summary = R.string.summary_checkbox_vibrate_fallback,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Flip face buttons",
                    category = "Gamepad",
                    title = R.string.title_checkbox_flip_face_buttons,
                    summary = R.string.summary_checkbox_flip_face_buttons,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Always control mouse with touchpad",
                    category = "Gamepad",
                    title = R.string.title_checkbox_gamepad_touchpad_as_mouse,
                    summary = R.string.summary_checkbox_gamepad_touchpad_as_mouse,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Allow use of gamepad motion sensors",
                    category = "Gamepad",
                    title = R.string.title_checkbox_gamepad_motion_sensors,
                    summary = R.string.summary_checkbox_gamepad_motion_sensors,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Emulate gamepad motion sensor support",
                    category = "Gamepad",
                    title = R.string.title_checkbox_gamepad_motion_fallback,
                    summary = R.string.summary_checkbox_gamepad_motion_fallback,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Mouse",
            categoryTitle = R.string.category_input_settings,
            icon = Icons.Outlined.Mouse,
            items = listOf(
                SettingItem.Toggle(
                    name = "Use the touchscreen as a trackpad",
                    category = "Mouse",
                    title = R.string.title_checkbox_touchscreen_trackpad,
                    summary = R.string.summary_checkbox_touchscreen_trackpad,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Enable back and forward mouse buttons",
                    category = "Mouse",
                    title = R.string.title_checkbox_mouse_nav_buttons,
                    summary = R.string.summary_checkbox_mouse_nav_buttons,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Remote desktop mouse mode",
                    category = "Mouse",
                    title = R.string.title_checkbox_absolute_mouse_mode,
                    summary = R.string.summary_checkbox_absolute_mouse_mode,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "On-screen Controls Settings",
            categoryTitle = R.string.category_on_screen_controls_settings,
            icon = Icons.Outlined.VideogameAsset,
            items = listOf(
                SettingItem.Toggle(
                    name = "Show on-screen controls",
                    category = "On-screen Controls Settings",
                    title = R.string.title_checkbox_show_onscreen_controls,
                    summary = R.string.summary_checkbox_show_onscreen_controls,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Host Settings",
            categoryTitle = R.string.category_host_settings,
            icon = Icons.Outlined.DesktopWindows,
            items = listOf(
                SettingItem.Toggle(
                    name = "Optimize game settings",
                    category = "Host Settings",
                    title = R.string.title_checkbox_enable_sops,
                    summary = R.string.summary_checkbox_enable_sops,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Play audio on PC",
                    category = "Host Settings",
                    title = R.string.title_checkbox_host_audio,
                    summary = R.string.summary_checkbox_host_audio,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "UI Settings",
            categoryTitle = R.string.category_ui_settings,
            icon = Icons.Outlined.ColorLens,
            items = listOf(
                SettingItem.Toggle(
                    name = "Enable Picture-in-Picture observer mode",
                    category = "Appearance",
                    title = R.string.title_checkbox_enable_pip,
                    summary = R.string.summary_checkbox_enable_pip,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Use small box art",
                    category = "Appearance",
                    title = R.string.title_checkbox_small_icon_mode,
                    summary = R.string.summary_checkbox_small_icon_mode,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Advanced Settings",
            categoryTitle = R.string.category_advanced_settings,
            icon = Icons.Outlined.Build,
            items = listOf(
                SettingItem.Toggle(
                    name = "Unlock all possible frame rates",
                    category = "Advanced",
                    title = R.string.title_unlock_fps,
                    summary = R.string.summary_unlock_fps,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Allow refresh rate reduction",
                    category = "Advanced",
                    title = R.string.title_checkbox_reduce_refresh_rate,
                    summary = R.string.summary_checkbox_reduce_refresh_rate,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Force full range video (Experimental)",
                    category = "Advanced",
                    title = R.string.title_full_range,
                    summary = R.string.summary_full_range,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Show performance stats while streaming",
                    category = "Advanced",
                    title = R.string.title_enable_perf_overlay,
                    summary = R.string.summary_enable_perf_overlay,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Show latency message after streaming",
                    category = "Advanced",
                    title = R.string.title_enable_post_stream_toast,
                    summary = R.string.summary_enable_post_stream_toast,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        )
    )
}

class SettingsRepository {
    fun getSettings(): List<SettingCategory> = SettingsData.categories
}
package com.limelight.viewmodel

import androidx.annotation.StringRes
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
import com.limelight.R

// Represents a single setting item
sealed class SettingItem {
    abstract val name: String
    abstract val category: String
    @get:StringRes abstract val headline: Int

    data class Toggle(
        override val name: String,
        override val category: String,
        override val headline: Int,
        val isEnabled: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItem()

    data class Slider(
        override val name: String,
        override val category: String,
        override val headline: Int,
        val value: Float,
        val range: ClosedFloatingPointRange<Float>,
        val onValueChange: (Float) -> Unit
    ) : SettingItem()

    // Add more types as needed (e.g., TextInput, Dropdown)
}

// Represents a category with its settings and icon
data class SettingCategory(
    @StringRes val headline: Int,
    val name: String,
    val icon: ImageVector = Icons.Default.Build,
    val items: List<SettingItem>,
)

// Centralized settings data source
object SettingsData {
    val categories = listOf(
        SettingCategory(
            name = "Video",
            headline = R.string.category_basic_settings,
            icon = Icons.Default.Face,
            items = listOf(
                SettingItem.Toggle(
                    name = "Enable Fullscreen",
                    category = "Video",
                    headline = R.string.title_checkbox_stretch_video,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Slider(
                    name ="Bitrate",
                    category = "Video",
                    headline = R.string.title_seekbar_bitrate,
                    value = 0.5f,
                    range = 0f..1f
                ) { /* Handle slider */ }
            ),
        ),
        SettingCategory(
            name = "Audio",
            headline = R.string.category_audio_settings,
            icon = Icons.Default.Notifications,
            items = listOf(
                SettingItem.Toggle(
                    name = "Mute",
                    category = "Audio",
                    headline = R.string.title_checkbox_enable_audiofx,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Gamepad",
            headline = R.string.category_gamepad_settings,
            icon = Icons.Default.Person,
            items = listOf(
                SettingItem.Toggle(
                    name = "Automatic gamepad presence detection",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_multi_controller,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Xbox 360/One USB gamepad driver",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_xb1_driver,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Override native Xbox gamepad support",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_usb_bind_all,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Mouse emulation via gamepad",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_mouse_emulation,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Emulate rumble support with vibration",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_vibrate_fallback,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Flip face buttons",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_flip_face_buttons,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Always control mouse with touchpad",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_gamepad_touchpad_as_mouse,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Allow use of gamepad motion sensors",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_gamepad_motion_sensors,
                    isEnabled = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Emulate gamepad motion sensor support",
                    category = "Gamepad",
                    headline = R.string.title_checkbox_gamepad_motion_fallback,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Mouse",
            headline = R.string.category_input_settings,
            icon = Icons.Default.AccountBox,
            items = listOf(
                SettingItem.Toggle(
                    name = "Use the touchscreen as a trackpad",
                    category = "Mouse",
                    headline = R.string.title_checkbox_touchscreen_trackpad,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Enable back and forward mouse buttons",
                    category = "Mouse",
                    headline = R.string.title_checkbox_mouse_nav_buttons,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Remote desktop mouse mode",
                    category = "Mouse",
                    headline = R.string.title_checkbox_absolute_mouse_mode,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "On-screen Controls Settings",
            headline = R.string.category_on_screen_controls_settings,
            icon = Icons.Default.AccountCircle,
            items = listOf(
                SettingItem.Toggle(
                    name = "Show on-screen controls",
                    category = "On-screen Controls Settings",
                    headline = R.string.title_checkbox_show_onscreen_controls,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Host Settings",
            headline = R.string.category_host_settings,
            icon = Icons.Default.Home,
            items = listOf(
                SettingItem.Toggle(
                    name = "Optimize game settings",
                    category = "Host Settings",
                    headline = R.string.title_checkbox_enable_sops,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Play audio on PC",
                    category = "Host Settings",
                    headline = R.string.title_checkbox_host_audio,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "UI Settings",
            headline = R.string.category_ui_settings,
            icon = Icons.Default.Star,
            items = listOf(
                SettingItem.Toggle(
                    name = "Enable Picture-in-Picture observer mode",
                    category = "Appearance",
                    headline = R.string.title_checkbox_enable_pip,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Use small box art",
                    category = "Appearance",
                    headline = R.string.title_checkbox_small_icon_mode,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Advanced Settings",
            headline = R.string.category_advanced_settings,
            icon = Icons.Default.Build,
            items = listOf(
                SettingItem.Toggle(
                    name = "Unlock all possible frame rates",
                    category = "Advanced",
                    headline = R.string.title_unlock_fps,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Allow refresh rate reduction",
                    category = "Advanced",
                    headline = R.string.title_checkbox_reduce_refresh_rate,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Disable warning messages",
                    category = "Advanced",
                    headline = R.string.title_checkbox_disable_warnings,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Force full range video (Experimental)",
                    category = "Advanced",
                    headline = R.string.title_full_range,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Show performance stats while streaming",
                    category = "Advanced",
                    headline = R.string.title_enable_perf_overlay,
                    isEnabled = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "Show latency message after streaming",
                    category = "Advanced",
                    headline = R.string.title_enable_post_stream_toast,
                    isEnabled = false
                ) { /* Handle toggle */ },
            ),
        )
    )
}
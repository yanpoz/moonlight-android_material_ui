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
        val default: Boolean,
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
                    name = "checkbox_stretch_video",
                    category = "Video",
                    title = R.string.title_checkbox_stretch_video,
                    summary = R.string.title_checkbox_stretch_video,
                    default = false
                ) { /* Handle toggle */ }
            ),
        ),
        SettingCategory(
            name = "Audio",
            categoryTitle = R.string.category_audio_settings,
            icon = Icons.AutoMirrored.Outlined.VolumeUp,
            items = listOf(
                SettingItem.Toggle(
                    name = "checkbox_enable_audiofx",
                    category = "Audio",
                    title = R.string.title_checkbox_enable_audiofx,
                    summary = R.string.summary_checkbox_enable_audiofx,
                    default = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Gamepad",
            categoryTitle = R.string.category_gamepad_settings,
            icon = Icons.Outlined.SportsEsports,
            items = listOf(
                SettingItem.Toggle(
                    name = "checkbox_multi_controller",
                    category = "Gamepad",
                    title = R.string.title_checkbox_multi_controller,
                    summary = R.string.summary_checkbox_multi_controller,
                    default = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_usb_driver",
                    category = "Gamepad",
                    title = R.string.title_checkbox_xb1_driver,
                    summary = R.string.summary_checkbox_xb1_driver,
                    default = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_usb_bind_all",
                    category = "Gamepad",
                    title = R.string.title_checkbox_usb_bind_all,
                    summary = R.string.summary_checkbox_usb_bind_all,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_mouse_emulation",
                    category = "Gamepad",
                    title = R.string.title_checkbox_mouse_emulation,
                    summary = R.string.summary_checkbox_mouse_emulation,
                    default = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_vibrate_fallback",
                    category = "Gamepad",
                    title = R.string.title_checkbox_vibrate_fallback,
                    summary = R.string.summary_checkbox_vibrate_fallback,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_flip_face_buttons",
                    category = "Gamepad",
                    title = R.string.title_checkbox_flip_face_buttons,
                    summary = R.string.summary_checkbox_flip_face_buttons,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_gamepad_touchpad_as_mouse",
                    category = "Gamepad",
                    title = R.string.title_checkbox_gamepad_touchpad_as_mouse,
                    summary = R.string.summary_checkbox_gamepad_touchpad_as_mouse,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_gamepad_motion_sensors",
                    category = "Gamepad",
                    title = R.string.title_checkbox_gamepad_motion_sensors,
                    summary = R.string.summary_checkbox_gamepad_motion_sensors,
                    default = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_gamepad_motion_fallback",
                    category = "Gamepad",
                    title = R.string.title_checkbox_gamepad_motion_fallback,
                    summary = R.string.summary_checkbox_gamepad_motion_fallback,
                    default = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Mouse",
            categoryTitle = R.string.category_input_settings,
            icon = Icons.Outlined.Mouse,
            items = listOf(
                SettingItem.Toggle(
                    name = "checkbox_touchscreen_trackpad",
                    category = "Mouse",
                    title = R.string.title_checkbox_touchscreen_trackpad,
                    summary = R.string.summary_checkbox_touchscreen_trackpad,
                    default = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_mouse_nav_buttons",
                    category = "Mouse",
                    title = R.string.title_checkbox_mouse_nav_buttons,
                    summary = R.string.summary_checkbox_mouse_nav_buttons,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_absolute_mouse_mode",
                    category = "Mouse",
                    title = R.string.title_checkbox_absolute_mouse_mode,
                    summary = R.string.summary_checkbox_absolute_mouse_mode,
                    default = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "On-screen Controls Settings",
            categoryTitle = R.string.category_on_screen_controls_settings,
            icon = Icons.Outlined.VideogameAsset,
            items = listOf(
                SettingItem.Toggle(
                    name = "checkbox_show_onscreen_controls",
                    category = "On-screen Controls Settings",
                    title = R.string.title_checkbox_show_onscreen_controls,
                    summary = R.string.summary_checkbox_show_onscreen_controls,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_vibrate_osc",
                    category = "On-screen Controls Settings",
                    title = R.string.title_checkbox_vibrate_osc,
                    summary = R.string.summary_checkbox_vibrate_osc,
                    default = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_only_show_L3R3",
                    category = "On-screen Controls Settings",
                    title = R.string.title_only_l3r3,
                    summary = R.string.summary_only_l3r3,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_show_guide_button",
                    category = "On-screen Controls Settings",
                    title = R.string.title_show_guide_button,
                    summary = R.string.summary_show_guide_button,
                    default = true
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Host Settings",
            categoryTitle = R.string.category_host_settings,
            icon = Icons.Outlined.DesktopWindows,
            items = listOf(
                SettingItem.Toggle(
                    name = "checkbox_enable_sops",
                    category = "Host Settings",
                    title = R.string.title_checkbox_enable_sops,
                    summary = R.string.summary_checkbox_enable_sops,
                    default = true
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_host_audio",
                    category = "Host Settings",
                    title = R.string.title_checkbox_host_audio,
                    summary = R.string.summary_checkbox_host_audio,
                    default = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "UI Settings",
            categoryTitle = R.string.category_ui_settings,
            icon = Icons.Outlined.ColorLens,
            items = listOf(
                SettingItem.Toggle(
                    name = "checkbox_enable_pip",
                    category = "Appearance",
                    title = R.string.title_checkbox_enable_pip,
                    summary = R.string.summary_checkbox_enable_pip,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_small_icon_mode",
                    category = "Appearance",
                    title = R.string.title_checkbox_small_icon_mode,
                    summary = R.string.summary_checkbox_small_icon_mode,
                    default = false
                ) { /* Handle toggle */ },
            ),
        ),
        SettingCategory(
            name = "Advanced Settings",
            categoryTitle = R.string.category_advanced_settings,
            icon = Icons.Outlined.Build,
            items = listOf(
                SettingItem.Toggle(
                    name = "checkbox_unlock_fps",
                    category = "Advanced",
                    title = R.string.title_unlock_fps,
                    summary = R.string.summary_unlock_fps,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_reduce_refresh_rate",
                    category = "Advanced",
                    title = R.string.title_checkbox_reduce_refresh_rate,
                    summary = R.string.summary_checkbox_reduce_refresh_rate,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_disable_warnings",
                    category = "Advanced",
                    title = R.string.title_checkbox_disable_warnings,
                    summary = R.string.summary_checkbox_disable_warnings,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_enable_hdr",
                    category = "Advanced",
                    title = R.string.title_enable_hdr,
                    summary = R.string.summary_enable_hdr,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_full_range",
                    category = "Advanced",
                    title = R.string.title_full_range,
                    summary = R.string.summary_full_range,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_enable_perf_overlay",
                    category = "Advanced",
                    title = R.string.title_enable_perf_overlay,
                    summary = R.string.summary_enable_perf_overlay,
                    default = false
                ) { /* Handle toggle */ },
                SettingItem.Toggle(
                    name = "checkbox_enable_post_stream_toast",
                    category = "Advanced",
                    title = R.string.title_enable_post_stream_toast,
                    summary = R.string.summary_enable_post_stream_toast,
                    default = false
                ) { /* Handle toggle */ },
            ),
        )
    )
}

class SettingsRepository {
    fun getSettings(): List<SettingCategory> = SettingsData.categories
}

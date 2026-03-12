package com.limelight.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Mouse
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.ui.graphics.vector.ImageVector
import com.limelight.R
import com.limelight.preferences.PreferenceConfiguration
import androidx.core.content.edit

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

    data class Selection(
        override val name: String,
        override val category: String,
        override val title: Int,
        override val summary: Int,
        val entries: List<String>,
        val entryValues: List<String>,
        val currentValue: String,
        val onSelected: (String) -> Unit
    ) : SettingItem()

    data class Slider(
        override val name: String,
        override val category: String,
        override val title: Int,
        override val summary: Int,
        val value: Float,
        val min: Float,
        val max: Float,
        @get:StringRes val unit: Int? = null,
        val onValueChange: (Float) -> Unit
    ) : SettingItem()

    data class Action(
        override val name: String,
        override val category: String,
        override val title: Int,
        override val summary: Int,
        val onClick: () -> Unit
    ) : SettingItem()
}

// Represents a category with its settings and icon
data class SettingCategory(
    @StringRes val categoryTitle: Int,
    val name: String,
    val icon: ImageVector = Icons.Default.Build,
    val items: List<SettingItem>,
)

class SettingsRepository(private val context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getSettings(): List<SettingCategory> {
        return listOf(
            SettingCategory(
                name = "Video",
                categoryTitle = R.string.category_basic_settings,
                icon = Icons.Outlined.Theaters,
                items = listOf(
                    SettingItem.Selection(
                        name = PreferenceConfiguration.RESOLUTION_PREF_STRING,
                        category = "Video",
                        title = R.string.title_resolution_list,
                        summary = R.string.summary_resolution_list,
                        entries = context.resources.getStringArray(R.array.resolution_names).toList(),
                        entryValues = context.resources.getStringArray(R.array.resolution_values).toList(),
                        currentValue = prefs.getString(PreferenceConfiguration.RESOLUTION_PREF_STRING, PreferenceConfiguration.DEFAULT_RESOLUTION) ?: PreferenceConfiguration.DEFAULT_RESOLUTION,
                        onSelected = { prefs.edit { putString(PreferenceConfiguration.RESOLUTION_PREF_STRING, it) } }
                    ),
                    SettingItem.Selection(
                        name = PreferenceConfiguration.FPS_PREF_STRING,
                        category = "Video",
                        title = R.string.title_fps_list,
                        summary = R.string.summary_fps_list,
                        entries = context.resources.getStringArray(R.array.fps_names).toList(),
                        entryValues = context.resources.getStringArray(R.array.fps_values).toList(),
                        currentValue = prefs.getString(PreferenceConfiguration.FPS_PREF_STRING, PreferenceConfiguration.DEFAULT_FPS) ?: PreferenceConfiguration.DEFAULT_FPS,
                        onSelected = { prefs.edit { putString(PreferenceConfiguration.FPS_PREF_STRING, it) } }
                    ),
                    SettingItem.Slider(
                        name = PreferenceConfiguration.BITRATE_PREF_STRING,
                        category = "Video",
                        title = R.string.title_seekbar_bitrate,
                        summary = R.string.summary_seekbar_bitrate,
                        value = prefs.getInt(PreferenceConfiguration.BITRATE_PREF_STRING, PreferenceConfiguration.getDefaultBitrate(context)).toFloat() / 1000f,
                        min = 0.5f,
                        max = 150f,
                        unit = R.string.suffix_seekbar_bitrate_mbps,
                        onValueChange = { prefs.edit { putInt(PreferenceConfiguration.BITRATE_PREF_STRING, (it * 1000).toInt()) } }
                    ),
                    SettingItem.Selection(
                        name = PreferenceConfiguration.FRAME_PACING_PREF_STRING,
                        category = "Video",
                        title = R.string.title_frame_pacing,
                        summary = R.string.summary_frame_pacing,
                        entries = context.resources.getStringArray(R.array.video_frame_pacing_names).toList(),
                        entryValues = context.resources.getStringArray(R.array.video_frame_pacing_values).toList(),
                        currentValue = prefs.getString(PreferenceConfiguration.FRAME_PACING_PREF_STRING, PreferenceConfiguration.DEFAULT_FRAME_PACING) ?: PreferenceConfiguration.DEFAULT_FRAME_PACING,
                        onSelected = { prefs.edit { putString(PreferenceConfiguration.FRAME_PACING_PREF_STRING, it) } }
                    ),
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.STRETCH_PREF_STRING,
                        category = "Video",
                        title = R.string.title_checkbox_stretch_video,
                        summary = R.string.title_checkbox_stretch_video,
                        default = prefs.getBoolean(PreferenceConfiguration.STRETCH_PREF_STRING, PreferenceConfiguration.DEFAULT_STRETCH)
                    ) { prefs.edit { putBoolean(PreferenceConfiguration.STRETCH_PREF_STRING, it) } }
                ),
            ),
            SettingCategory(
                name = "Audio",
                categoryTitle = R.string.category_audio_settings,
                icon = Icons.AutoMirrored.Outlined.VolumeUp,
                items = listOf(
                    SettingItem.Selection(
                        name = PreferenceConfiguration.AUDIO_CONFIG_PREF_STRING,
                        category = "Audio",
                        title = R.string.title_audio_config_list,
                        summary = R.string.summary_audio_config_list,
                        entries = context.resources.getStringArray(R.array.audio_config_names).toList(),
                        entryValues = context.resources.getStringArray(R.array.audio_config_values).toList(),
                        currentValue = prefs.getString(PreferenceConfiguration.AUDIO_CONFIG_PREF_STRING, PreferenceConfiguration.DEFAULT_AUDIO_CONFIG) ?: PreferenceConfiguration.DEFAULT_AUDIO_CONFIG,
                        onSelected = { prefs.edit { putString(PreferenceConfiguration.AUDIO_CONFIG_PREF_STRING, it) } }
                    ),
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.ENABLE_AUDIO_FX_PREF_STRING,
                        category = "Audio",
                        title = R.string.title_checkbox_enable_audiofx,
                        summary = R.string.summary_checkbox_enable_audiofx,
                        default = prefs.getBoolean(PreferenceConfiguration.ENABLE_AUDIO_FX_PREF_STRING, PreferenceConfiguration.DEFAULT_ENABLE_AUDIO_FX)
                    ) { prefs.edit { putBoolean(PreferenceConfiguration.ENABLE_AUDIO_FX_PREF_STRING, it) } },
                ),
            ),
            SettingCategory(
                name = "Gamepad",
                categoryTitle = R.string.category_gamepad_settings,
                icon = Icons.Outlined.SportsEsports,
                items = listOf(
                    SettingItem.Slider(
                        name = PreferenceConfiguration.DEADZONE_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_seekbar_deadzone,
                        summary = R.string.summary_seekbar_deadzone,
                        value = prefs.getInt(PreferenceConfiguration.DEADZONE_PREF_STRING, PreferenceConfiguration.DEFAULT_DEADZONE).toFloat(),
                        min = 0f,
                        max = 20f,
                        unit = R.string.suffix_seekbar_deadzone,
                        onValueChange = { prefs.edit { putInt(PreferenceConfiguration.DEADZONE_PREF_STRING, it.toInt()) } }
                    ),
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.MULTI_CONTROLLER_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_multi_controller,
                        summary = R.string.summary_checkbox_multi_controller,
                        default = prefs.getBoolean(PreferenceConfiguration.MULTI_CONTROLLER_PREF_STRING, PreferenceConfiguration.DEFAULT_MULTI_CONTROLLER)
                    ) { prefs.edit { putBoolean(PreferenceConfiguration.MULTI_CONTROLLER_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.USB_DRIVER_PREF_SRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_xb1_driver,
                        summary = R.string.summary_checkbox_xb1_driver,
                        default = prefs.getBoolean(PreferenceConfiguration.USB_DRIVER_PREF_SRING, PreferenceConfiguration.DEFAULT_USB_DRIVER)
                    ) { prefs.edit { putBoolean(PreferenceConfiguration.USB_DRIVER_PREF_SRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.BIND_ALL_USB_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_usb_bind_all,
                        summary = R.string.summary_checkbox_usb_bind_all,
                        default = prefs.getBoolean(PreferenceConfiguration.BIND_ALL_USB_STRING, PreferenceConfiguration.DEFAULT_BIND_ALL_USB)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.BIND_ALL_USB_STRING, it)} },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.MOUSE_EMULATION_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_mouse_emulation,
                        summary = R.string.summary_checkbox_mouse_emulation,
                        default = prefs.getBoolean(PreferenceConfiguration.MOUSE_EMULATION_STRING, PreferenceConfiguration.DEFAULT_MOUSE_EMULATION)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.MOUSE_EMULATION_STRING, it)} },
                    SettingItem.Selection(
                        name = PreferenceConfiguration.ANALOG_SCROLLING_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_analog_scrolling,
                        summary = R.string.summary_analog_scrolling,
                        entries = context.resources.getStringArray(R.array.analog_scrolling_names).toList(),
                        entryValues = context.resources.getStringArray(R.array.analog_scrolling_values).toList(),
                        currentValue = prefs.getString(PreferenceConfiguration.ANALOG_SCROLLING_PREF_STRING, PreferenceConfiguration.DEFAULT_ANALOG_STICK_FOR_SCROLLING) ?: PreferenceConfiguration.DEFAULT_ANALOG_STICK_FOR_SCROLLING,
                        onSelected = { prefs.edit { putString(PreferenceConfiguration.ANALOG_SCROLLING_PREF_STRING, it) } }
                    ),
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.VIBRATE_FALLBACK_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_vibrate_fallback,
                        summary = R.string.summary_checkbox_vibrate_fallback,
                        default = prefs.getBoolean(PreferenceConfiguration.VIBRATE_FALLBACK_PREF_STRING, PreferenceConfiguration.DEFAULT_VIBRATE_FALLBACK)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.VIBRATE_FALLBACK_PREF_STRING, it)} },
                    SettingItem.Slider(
                        name = PreferenceConfiguration.VIBRATE_FALLBACK_STRENGTH_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_seekbar_vibrate_fallback_strength,
                        summary = R.string.summary_seekbar_vibrate_fallback_strength,
                        value = prefs.getInt(PreferenceConfiguration.VIBRATE_FALLBACK_STRENGTH_PREF_STRING, PreferenceConfiguration.DEFAULT_VIBRATE_FALLBACK_STRENGTH).toFloat(),
                        min = 0f,
                        max = 200f,
                        unit = R.string.suffix_seekbar_vibrate_fallback_strength,
                        onValueChange = { prefs.edit { putInt(PreferenceConfiguration.VIBRATE_FALLBACK_STRENGTH_PREF_STRING, it.toInt()) } }
                    ),
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.FLIP_FACE_BUTTONS_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_flip_face_buttons,
                        summary = R.string.summary_checkbox_flip_face_buttons,
                        default = prefs.getBoolean(PreferenceConfiguration.FLIP_FACE_BUTTONS_PREF_STRING, PreferenceConfiguration.DEFAULT_FLIP_FACE_BUTTONS)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.FLIP_FACE_BUTTONS_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.GAMEPAD_TOUCHPAD_AS_MOUSE_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_gamepad_touchpad_as_mouse,
                        summary = R.string.summary_checkbox_gamepad_touchpad_as_mouse,
                        default = prefs.getBoolean(PreferenceConfiguration.GAMEPAD_TOUCHPAD_AS_MOUSE_PREF_STRING, PreferenceConfiguration.DEFAULT_GAMEPAD_TOUCHPAD_AS_MOUSE)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.GAMEPAD_TOUCHPAD_AS_MOUSE_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.GAMEPAD_MOTION_SENSORS_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_gamepad_motion_sensors,
                        summary = R.string.summary_checkbox_gamepad_motion_sensors,
                        default = prefs.getBoolean(PreferenceConfiguration.GAMEPAD_MOTION_SENSORS_PREF_STRING, PreferenceConfiguration.DEFAULT_GAMEPAD_MOTION_SENSORS)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.GAMEPAD_MOTION_SENSORS_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.GAMEPAD_MOTION_FALLBACK_PREF_STRING,
                        category = "Gamepad",
                        title = R.string.title_checkbox_gamepad_motion_fallback,
                        summary = R.string.summary_checkbox_gamepad_motion_fallback,
                        default = prefs.getBoolean(PreferenceConfiguration.GAMEPAD_MOTION_FALLBACK_PREF_STRING, PreferenceConfiguration.DEFAULT_GAMEPAD_MOTION_FALLBACK)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.GAMEPAD_MOTION_FALLBACK_PREF_STRING, it) } },
                ),
            ),
            SettingCategory(
                name = "Mouse",
                categoryTitle = R.string.category_input_settings,
                icon = Icons.Outlined.Mouse,
                items = listOf(
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.TOUCHSCREEN_TRACKPAD_PREF_STRING,
                        category = "Mouse",
                        title = R.string.title_checkbox_touchscreen_trackpad,
                        summary = R.string.summary_checkbox_touchscreen_trackpad,
                        default = prefs.getBoolean(PreferenceConfiguration.TOUCHSCREEN_TRACKPAD_PREF_STRING, PreferenceConfiguration.DEFAULT_TOUCHSCREEN_TRACKPAD)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.TOUCHSCREEN_TRACKPAD_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.MOUSE_NAV_BUTTONS_STRING,
                        category = "Mouse",
                        title = R.string.title_checkbox_mouse_nav_buttons,
                        summary = R.string.summary_checkbox_mouse_nav_buttons,
                        default = prefs.getBoolean(PreferenceConfiguration.MOUSE_NAV_BUTTONS_STRING, PreferenceConfiguration.DEFAULT_MOUSE_NAV_BUTTONS)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.MOUSE_NAV_BUTTONS_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.ABSOLUTE_MOUSE_MODE_PREF_STRING,
                        category = "Mouse",
                        title = R.string.title_checkbox_absolute_mouse_mode,
                        summary = R.string.summary_checkbox_absolute_mouse_mode,
                        default = prefs.getBoolean(PreferenceConfiguration.ABSOLUTE_MOUSE_MODE_PREF_STRING, PreferenceConfiguration.DEFAULT_ABSOLUTE_MOUSE_MODE)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.ABSOLUTE_MOUSE_MODE_PREF_STRING, it) } },
                ),
            ),
            SettingCategory(
                name = "On-screen Controls Settings",
                categoryTitle = R.string.category_on_screen_controls_settings,
                icon = Icons.Outlined.VideogameAsset,
                items = listOf(
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.ONSCREEN_CONTROLLER_PREF_STRING,
                        category = "On-screen Controls Settings",
                        title = R.string.title_checkbox_show_onscreen_controls,
                        summary = R.string.summary_checkbox_show_onscreen_controls,
                        default = prefs.getBoolean(PreferenceConfiguration.ONSCREEN_CONTROLLER_PREF_STRING, PreferenceConfiguration.ONSCREEN_CONTROLLER_DEFAULT)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.ONSCREEN_CONTROLLER_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.VIBRATE_OSC_PREF_STRING,
                        category = "On-screen Controls Settings",
                        title = R.string.title_checkbox_vibrate_osc,
                        summary = R.string.summary_checkbox_vibrate_osc,
                        default = prefs.getBoolean(PreferenceConfiguration.VIBRATE_OSC_PREF_STRING, PreferenceConfiguration.DEFAULT_VIBRATE_OSC)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.VIBRATE_OSC_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.ONLY_L3_R3_PREF_STRING,
                        category = "On-screen Controls Settings",
                        title = R.string.title_only_l3r3,
                        summary = R.string.summary_only_l3r3,
                        default = prefs.getBoolean(PreferenceConfiguration.ONLY_L3_R3_PREF_STRING, PreferenceConfiguration.ONLY_L3_R3_DEFAULT)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.ONLY_L3_R3_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.SHOW_GUIDE_BUTTON_PREF_STRING,
                        category = "On-screen Controls Settings",
                        title = R.string.title_show_guide_button,
                        summary = R.string.summary_show_guide_button,
                        default = prefs.getBoolean(PreferenceConfiguration.SHOW_GUIDE_BUTTON_PREF_STRING, PreferenceConfiguration.SHOW_GUIDE_BUTTON_DEFAULT)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.SHOW_GUIDE_BUTTON_PREF_STRING, it) } },
                    SettingItem.Slider(
                        name = PreferenceConfiguration.OSC_OPACITY_PREF_STRING,
                        category = "On-screen Controls Settings",
                        title = R.string.dialog_title_osc_opacity,
                        summary = R.string.summary_osc_opacity,
                        value = prefs.getInt(PreferenceConfiguration.OSC_OPACITY_PREF_STRING, PreferenceConfiguration.DEFAULT_OPACITY).toFloat(),
                        min = 0f,
                        max = 100f,
                        unit = R.string.suffix_osc_opacity,
                        onValueChange = { prefs.edit { putInt(PreferenceConfiguration.OSC_OPACITY_PREF_STRING, it.toInt()) } }
                    ),
                ),
            ),
            SettingCategory(
                name = "Host Settings",
                categoryTitle = R.string.category_host_settings,
                icon = Icons.Outlined.DesktopWindows,
                items = listOf(
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.SOPS_PREF_STRING,
                        category = "Host Settings",
                        title = R.string.title_checkbox_enable_sops,
                        summary = R.string.summary_checkbox_enable_sops,
                        default = prefs.getBoolean(PreferenceConfiguration.SOPS_PREF_STRING, PreferenceConfiguration.DEFAULT_SOPS)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.SOPS_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.HOST_AUDIO_PREF_STRING,
                        category = "Host Settings",
                        title = R.string.title_checkbox_host_audio,
                        summary = R.string.summary_checkbox_host_audio,
                        default = prefs.getBoolean(PreferenceConfiguration.HOST_AUDIO_PREF_STRING, PreferenceConfiguration.DEFAULT_HOST_AUDIO)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.HOST_AUDIO_PREF_STRING, it) } },
                ),
            ),
            SettingCategory(
                name = "UI Settings",
                categoryTitle = R.string.category_ui_settings,
                icon = Icons.Outlined.ColorLens,
                items = listOf(
                    SettingItem.Selection(
                        name = PreferenceConfiguration.THEME_PREF_STRING,
                        category = "UI Settings",
                        title = R.string.title_checkbox_enable_pip,
                        summary = R.string.summary_checkbox_enable_pip,
                        entries = listOf("Light", "Dark", "System"),
                        entryValues = listOf("light", "dark", "system"),
                        currentValue = prefs.getString(PreferenceConfiguration.THEME_PREF_STRING, PreferenceConfiguration.DEFAULT_THEME) ?: PreferenceConfiguration.DEFAULT_THEME,
                        onSelected = { prefs.edit { putString(PreferenceConfiguration.THEME_PREF_STRING, it) } }
                    ),
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.ENABLE_PIP_PREF_STRING,
                        category = "Appearance",
                        title = R.string.title_checkbox_enable_pip,
                        summary = R.string.summary_checkbox_enable_pip,
                        default = prefs.getBoolean(PreferenceConfiguration.ENABLE_PIP_PREF_STRING, PreferenceConfiguration.DEFAULT_ENABLE_PIP)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.ENABLE_PIP_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.SMALL_ICONS_PREF_STRING,
                        category = "Appearance",
                        title = R.string.title_checkbox_small_icon_mode,
                        summary = R.string.summary_checkbox_small_icon_mode,
                        default = prefs.getBoolean(PreferenceConfiguration.SMALL_ICONS_PREF_STRING, PreferenceConfiguration.getDefaultSmallMode(context))
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.SMALL_ICONS_PREF_STRING, it) } },
                    SettingItem.Action(
                        name = "list_languages",
                        category = "Appearance",
                        title = R.string.title_language_list,
                        summary = R.string.summary_language_list,
                    ) {
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        } else {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                ),
            ),
            SettingCategory(
                name = "Advanced Settings",
                categoryTitle = R.string.category_advanced_settings,
                icon = Icons.Outlined.Build,
                items = listOf(
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.UNLOCK_FPS_STRING,
                        category = "Advanced",
                        title = R.string.title_unlock_fps,
                        summary = R.string.summary_unlock_fps,
                        default = prefs.getBoolean(PreferenceConfiguration.UNLOCK_FPS_STRING, PreferenceConfiguration.DEFAULT_UNLOCK_FPS)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.UNLOCK_FPS_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.REDUCE_REFRESH_RATE_PREF_STRING,
                        category = "Advanced",
                        title = R.string.title_checkbox_reduce_refresh_rate,
                        summary = R.string.summary_checkbox_reduce_refresh_rate,
                        default = prefs.getBoolean(PreferenceConfiguration.REDUCE_REFRESH_RATE_PREF_STRING, PreferenceConfiguration.DEFAULT_REDUCE_REFRESH_RATE)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.REDUCE_REFRESH_RATE_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.DISABLE_TOASTS_PREF_STRING,
                        category = "Advanced",
                        title = R.string.title_checkbox_disable_warnings,
                        summary = R.string.summary_checkbox_disable_warnings,
                        default = prefs.getBoolean(PreferenceConfiguration.DISABLE_TOASTS_PREF_STRING, PreferenceConfiguration.DEFAULT_DISABLE_TOASTS)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.DISABLE_TOASTS_PREF_STRING, it) } },
                    SettingItem.Selection(
                        name = PreferenceConfiguration.VIDEO_FORMAT_PREF_STRING,
                        category = "Advanced",
                        title = R.string.title_video_format,
                        summary = R.string.summary_video_format,
                        entries = context.resources.getStringArray(R.array.video_format_names).toList(),
                        entryValues = context.resources.getStringArray(R.array.video_format_values).toList(),
                        currentValue = prefs.getString(PreferenceConfiguration.VIDEO_FORMAT_PREF_STRING, PreferenceConfiguration.DEFAULT_VIDEO_FORMAT) ?: PreferenceConfiguration.DEFAULT_VIDEO_FORMAT,
                        onSelected = { prefs.edit { putString(PreferenceConfiguration.VIDEO_FORMAT_PREF_STRING, it) } }
                    ),
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.ENABLE_HDR_PREF_STRING,
                        category = "Advanced",
                        title = R.string.title_enable_hdr,
                        summary = R.string.summary_enable_hdr,
                        default = prefs.getBoolean(PreferenceConfiguration.ENABLE_HDR_PREF_STRING, PreferenceConfiguration.DEFAULT_ENABLE_HDR)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.ENABLE_HDR_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.FULL_RANGE_PREF_STRING,
                        category = "Advanced",
                        title = R.string.title_full_range,
                        summary = R.string.summary_full_range,
                        default = prefs.getBoolean(PreferenceConfiguration.FULL_RANGE_PREF_STRING, PreferenceConfiguration.DEFAULT_FULL_RANGE)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.FULL_RANGE_PREF_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.ENABLE_PERF_OVERLAY_STRING,
                        category = "Advanced",
                        title = R.string.title_enable_perf_overlay,
                        summary = R.string.summary_enable_perf_overlay,
                        default = prefs.getBoolean(PreferenceConfiguration.ENABLE_PERF_OVERLAY_STRING, PreferenceConfiguration.DEFAULT_ENABLE_PERF_OVERLAY)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.ENABLE_PERF_OVERLAY_STRING, it) } },
                    SettingItem.Toggle(
                        name = PreferenceConfiguration.LATENCY_TOAST_PREF_STRING,
                        category = "Advanced",
                        title = R.string.title_enable_post_stream_toast,
                        summary = R.string.summary_enable_post_stream_toast,
                        default = prefs.getBoolean(PreferenceConfiguration.LATENCY_TOAST_PREF_STRING, PreferenceConfiguration.DEFAULT_LATENCY_TOAST)
                    ) { prefs.edit {putBoolean(PreferenceConfiguration.LATENCY_TOAST_PREF_STRING, it) } },
                ),
            )
        )
    }
}

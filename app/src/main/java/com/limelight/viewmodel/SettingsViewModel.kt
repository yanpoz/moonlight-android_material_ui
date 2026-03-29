package com.limelight.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.limelight.repository.SettingCategory
import com.limelight.repository.SettingItem
import com.limelight.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SettingsUiState(
    val categories: List<SettingCategory> = emptyList(),
    val selectedCategory: SettingCategory? = null,
    val openSelectionDialog: SettingItem.Selection? = null,
    val openSliderDialog: SettingItem.Slider? = null
)

data class SettingsActions(
    val onCategorySelected: (SettingCategory) -> Unit = {},
    val onSettingToggled: (String, String, Boolean) -> Unit = { _, _, _ -> },
    val onSettingSelected: (String, String, String) -> Unit = { _, _, _ -> },
    val onSelectionItemClick: (SettingItem.Selection) -> Unit = {},
    val onSliderItemClick: (SettingItem.Slider) -> Unit = {},
    val onDismissSelectionDialog: () -> Unit = {},
    val onDismissSliderDialog: () -> Unit = {},
    val onSliderValueChanged: (String, String, Float) -> Unit = { _, _, _ -> }
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SettingsRepository = SettingsRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val settings = repository.getSettings()
        _uiState.value = SettingsUiState(categories = settings, selectedCategory = null)
    }

    fun selectCategory(category: SettingCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onSettingToggled(categoryName: String, settingName: String, isEnabled: Boolean) {
        val updatedCategories = uiState.value.categories.map { settingCategory ->
            if (settingCategory.name == categoryName) {
                settingCategory.copy(items = settingCategory.items.map {
                    if (it.name == settingName && it is SettingItem.Toggle) {
                        it.copy(default = isEnabled)
                    } else {
                        it
                    }
                })
            } else {
                settingCategory
            }
        }

        val updatedSelectedCategory = updatedCategories.find { it.name == categoryName }

        // Find the specific setting and call its onToggle lambda
        updatedSelectedCategory?.items?.find { it.name == settingName }?.let {
            if (it is SettingItem.Toggle) {
                it.onToggle(isEnabled)
            }
        }

        _uiState.value = uiState.value.copy(
            categories = updatedCategories,
            selectedCategory = updatedSelectedCategory ?: uiState.value.selectedCategory
        )
    }

    fun onSettingSelected(categoryName: String, settingName: String, newValue: String) {
        val updatedCategories = uiState.value.categories.map { settingCategory ->
            if (settingCategory.name == categoryName) {
                settingCategory.copy(items = settingCategory.items.map {
                    if (it.name == settingName && it is SettingItem.Selection) {
                        it.copy(currentValue = newValue)
                    } else {
                        it
                    }
                })
            } else {
                settingCategory
            }
        }

        val updatedSelectedCategory = updatedCategories.find { it.name == categoryName }

        // Find the specific setting and call its onSelected lambda
        updatedSelectedCategory?.items?.find { it.name == settingName }?.let {
            if (it is SettingItem.Selection) {
                it.onSelected(newValue)
            }
        }

        _uiState.value = uiState.value.copy(
            categories = updatedCategories,
            selectedCategory = updatedSelectedCategory ?: uiState.value.selectedCategory,
            openSelectionDialog = null
        )
    }

    fun onSliderValueChanged(categoryName: String, settingName: String, newValue: Float) {
        val updatedCategories = uiState.value.categories.map { settingCategory ->
            if (settingCategory.name == categoryName) {
                settingCategory.copy(items = settingCategory.items.map {
                    if (it.name == settingName && it is SettingItem.Slider) {
                        it.copy(value = newValue)
                    } else {
                        it
                    }
                })
            } else {
                settingCategory
            }
        }

        val updatedSelectedCategory = updatedCategories.find { it.name == categoryName }

        // Find the specific setting and call its onValueChange lambda
        updatedSelectedCategory?.items?.find { it.name == settingName }?.let {
            if (it is SettingItem.Slider) {
                it.onValueChange(newValue)
            }
        }

        _uiState.value = uiState.value.copy(
            categories = updatedCategories,
            selectedCategory = updatedSelectedCategory ?: uiState.value.selectedCategory,
            openSliderDialog = null
        )
    }

    fun showSelectionDialog(item: SettingItem.Selection) {
        _uiState.value = _uiState.value.copy(openSelectionDialog = item)
    }

    fun dismissSelectionDialog() {
        _uiState.value = _uiState.value.copy(openSelectionDialog = null)
    }

    fun showSliderDialog(item: SettingItem.Slider) {
        _uiState.value = _uiState.value.copy(openSliderDialog = item)
    }

    fun dismissSliderDialog() {
        _uiState.value = _uiState.value.copy(openSliderDialog = null)
    }
}

package com.limelight.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.limelight.repository.SettingCategory
import com.limelight.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
                    if (it.name == settingName) {
                        (it as com.limelight.repository.SettingItem.Toggle).copy(default = isEnabled)
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
            if (it is com.limelight.repository.SettingItem.Toggle) {
                it.onToggle(isEnabled)
            }
        }

        _uiState.value = uiState.value.copy(
            categories = updatedCategories,
            selectedCategory = updatedSelectedCategory ?: uiState.value.selectedCategory
        )
    }
}

data class SettingsUiState(
    val categories: List<SettingCategory> = emptyList(),
    val selectedCategory: SettingCategory? = null
)

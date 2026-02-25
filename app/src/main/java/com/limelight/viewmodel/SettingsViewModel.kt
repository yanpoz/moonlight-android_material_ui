package com.limelight.viewmodel

import androidx.lifecycle.ViewModel
import com.limelight.repository.SettingCategory
import com.limelight.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val repository: SettingsRepository = SettingsRepository()) : ViewModel() {

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
        // In a real app, you would have logic here to persist the setting
        // and then update the UI state.
    }
}

data class SettingsUiState(
    val categories: List<SettingCategory> = emptyList(),
    val selectedCategory: SettingCategory? = null
)

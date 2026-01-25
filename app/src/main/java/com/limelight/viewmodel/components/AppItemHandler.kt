package com.limelight.viewmodel.components

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.limelight.nvstream.http.NvApp

data class AppMenuUiState(
    val appId: Int? = null,
    val computerUuid: String? = null,
)

data class AppViewDetailsUiState(
    val showDialog: Boolean = false,
    val app: NvApp? = null,
)

class AppItemHandler(
    private val confirmationHandler: ConfirmationHandler,
    private val quitApp: (Context, NvApp, String) -> Unit,)
{
    var menu by mutableStateOf(AppMenuUiState())
        private set
    var viewDetails by mutableStateOf(AppViewDetailsUiState())
        private set

    fun isMenuExpanded(appId: Int, computerUuid: String): Boolean =
        menu.appId == appId && menu.computerUuid == computerUuid

    fun onOpenMenu(appId: Int, computerUuid: String) {
        menu = AppMenuUiState(appId, computerUuid)
    }
    fun onDismissMenu() {
        menu = AppMenuUiState()
    }
    fun onDetailsClicked(app: NvApp) {
        viewDetails = AppViewDetailsUiState(true, app)
    }
    fun onDismissDetailsDialog() {
        viewDetails = AppViewDetailsUiState()
    }
    fun onQuitApp(context: Context, app: NvApp, computerUuid: String) {
        confirmationHandler.confirmAction(
            title = "Quit ${app.appName}?",
            text = "Are you sure you want to quit ${app.appName}?",
            action = { quitApp(context, app, computerUuid) }
        )
    }
}
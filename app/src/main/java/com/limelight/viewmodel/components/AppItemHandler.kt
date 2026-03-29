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
    private val quitApp: (Context, NvApp, String) -> Unit,
    private val moveUp: (String, Int) -> Unit,
    private val moveDown: (String, Int) -> Unit,
)
{
    var menuUiState by mutableStateOf(AppMenuUiState())
        private set
    var viewDetailsUiState by mutableStateOf(AppViewDetailsUiState())
        private set

    fun isMenuExpanded(appId: Int, computerUuid: String): Boolean =
        menuUiState.appId == appId && menuUiState.computerUuid == computerUuid

    fun onOpenMenu(appId: Int, computerUuid: String) {
        menuUiState = AppMenuUiState(appId, computerUuid)
    }
    fun onDismissMenu() {
        menuUiState = AppMenuUiState()
    }
    fun onMoveUp(computerUuid: String, appId: Int) {
        moveUp(computerUuid, appId)
    }
    fun onMoveDown(computerUuid: String, appId: Int) {
        moveDown(computerUuid, appId)
    }
    fun onDetailsClicked(app: NvApp) {
        viewDetailsUiState = AppViewDetailsUiState(true, app)
    }
    fun onDismissDetailsDialog() {
        viewDetailsUiState = AppViewDetailsUiState()
    }
    fun onQuitApp(context: Context, app: NvApp, computerUuid: String) {
        confirmationHandler.confirmAction(
            title = "Quit ${app.appName}?",
            text = "Are you sure you want to quit ${app.appName}?",
            action = { quitApp(context, app, computerUuid) }
        )
    }
}
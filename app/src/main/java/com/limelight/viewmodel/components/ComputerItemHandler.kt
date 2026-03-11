package com.limelight.viewmodel.components

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.limelight.computers.Computer


data class ComputerMenuUiState(
    val computerUuid: String? = null,
)
data class ComputerViewDetailsUiState(
    val showDialog: Boolean = false,
    val computer: Computer? = null,
)


class ComputerItemHandler(
    private val confirmationHandler: ConfirmationHandler,
    private val quitRunningApp: (Context, Computer) -> Unit,
    private val sendWakeOnLan: (Context, String) -> Unit,
    private val moveUp: (String) -> Unit,
    private val moveDown: (String) -> Unit,
)
{
    var uiState by mutableStateOf(ComputerMenuUiState())
        private set
    var viewDetails by mutableStateOf(ComputerViewDetailsUiState())
        private set

    fun isMenuExpanded(computerUuid: String): Boolean = uiState.computerUuid == computerUuid

    fun onOpenMenu(computerUuid: String) {
        uiState = ComputerMenuUiState(computerUuid)
    }
    fun onDismissMenu() {
        uiState = ComputerMenuUiState()
    }
    fun onViewDetailsClicked(computer: Computer) {
        viewDetails = ComputerViewDetailsUiState(true, computer)
    }
    fun onDismissDetailsDialog() {
        viewDetails = ComputerViewDetailsUiState()
    }
    fun onQuitRunningApp(context: Context, computer: Computer) {
        computer.getRunningApp()?.let { app ->
            confirmationHandler.confirmAction(
                title = "Quit ${app.appName}?",
                text = "Are you sure you want to quit ${app.appName}?",
                action = { quitRunningApp(context, computer) }
            )
        }
    }
    fun onSendWakeOnLan(context: Context, computerUuid: String) {
        sendWakeOnLan(context, computerUuid)
    }
    fun onMoveUp(computerUuid: String) {
        moveUp(computerUuid)
    }
    fun onMoveDown(computerUuid: String) {
        moveDown(computerUuid)
    }
}
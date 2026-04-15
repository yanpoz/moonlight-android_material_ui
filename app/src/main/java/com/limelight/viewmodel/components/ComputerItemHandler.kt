package com.limelight.viewmodel.components

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.limelight.computers.Computer

enum class StatusShapeState {
    Far, Near, Orbit
}

data class StatusIndicatorUiState(
    val atmosphereSize: Dp,
    val atmosphereColor: Color,
    val planetSize: Dp,
    val planetColor: Color,
    val offsetX: Dp,
    val offsetY: Dp
)

data class StatusTextUiState(
    val text: String,
    val textColor: Color
)

data class ComputerItemUiState(
    val name: String,
    val address: String,
    val actionText: String,
    val actionTextColor: Color,
    val statusIndicator: StatusIndicatorUiState,
    val statusText: StatusTextUiState
)

data class ComputerMenuUiState(
    val computerUuid: String? = null,
)
data class ComputerViewDetailsUiState(
    val showDialog: Boolean = false,
    val computer: Computer? = null,
)
data class NetworkTestUiState(
    val showDialog: Boolean = false,
)


class ComputerItemHandler(
    private val confirmationHandler: ConfirmationHandler,
    private val quitRunningApp: (Context, Computer) -> Unit,
    private val sendWakeOnLan: (Context, String) -> Unit,
    private val moveUp: (String) -> Unit,
    private val moveDown: (String) -> Unit,
    private val deleteComputer: (String) -> Unit,
    private val testNetwork: (Context) -> Unit,
    private val dismissNetworkTest: () -> Unit,
)
{
    var menuUiState by mutableStateOf(ComputerMenuUiState())
        private set
    var viewDetailsUiState by mutableStateOf(ComputerViewDetailsUiState())
        private set
    var networkTestUiState by mutableStateOf(NetworkTestUiState())
        private set

    fun onOpenMenu(computerUuid: String) {
        menuUiState = ComputerMenuUiState(computerUuid)
    }
    fun onDismissMenu() {
        menuUiState = ComputerMenuUiState()
    }
    fun onViewDetailsClicked(computer: Computer) {
        viewDetailsUiState = ComputerViewDetailsUiState(true, computer)
    }
    fun onDismissDetailsDialog() {
        viewDetailsUiState = ComputerViewDetailsUiState()
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
    fun onDeleteComputer(computer: Computer) {
        confirmationHandler.confirmAction(
            title = "Delete ${computer.details.name}?",
            text = "Are you sure you want to delete ${computer.details.name}?",
            action = { deleteComputer(computer.details.uuid) }
        )
    }
    fun onTestNetwork(context: Context) {
        networkTestUiState = NetworkTestUiState(true)
        testNetwork(context)
    }
    fun onDismissNetworkTest() {
        networkTestUiState = NetworkTestUiState(false)
        dismissNetworkTest()
    }
}
